package advancedshader.core.patcher.vanilla;

import org.lwjgl.opengl.GL11;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * VertexBuffer is bnt in 1.10.2 (1.12.2 calls this class BufferBuilder).
 *
 * Only the patches whose anchor exists in the 1.10.2 body are kept. The 1.10.2 class is
 * materially simpler than its 1.12.2 descendant:
 *
 *   * draw(int,int) is `iload_2, iload_1, imul, istore_3` followed by two
 *     `iconst_4, imul` pairs and a single glDrawArrays. There is no multi-texture
 *     branch to extend with a triangle mode, so the upstream draw patch, which rewrites
 *     that branch, has nothing to anchor on.
 *   * quadsToTriangles begins with the quad-mode test the line-mode patch needs, so that
 *     one is ported unchanged.
 *
 * The class is patched as a whole or not at all, so a patch that cannot apply would
 * discard the others; that is why the unsupported ones are omitted here rather than
 * attempted and allowed to fail.
 */
@Patch("bnt")
public class BufferBuilderPatcher extends Patcher {

    // GL_LINES (1) is added as an accepted draw mode next to GL_QUADS (7).
    @MethodPatch("quadsToTriangles()V")
    public void quadsToTriangles(MethodNode method) {
        JumpInsnNode jmp = ByteCode.IfIntEqual(null);

        jmp.label = (LabelNode) patch("line draw mode support", method,
                ByteCode.ALoad(0),
                ByteCode.GetField("bnt", "j", "I"),
                ByteCode.BIPush(GL11.GL_QUADS),
                ByteCode.IfIntEqual(null),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("bnt", "j", "I")),
                inject(ByteCode.IConst(GL11.GL_LINES)),
                inject(jmp),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Return(),
                collect(ByteCode.Label()))[0];
    }

    // endVertex()V is d()V in 1.10.2.
    @MethodPatch("d()V")
    public void endVertex(MethodNode method) {
        patch("extra vertex for line strips", method,
                ByteCode.InvokeStatic("net/optifine/shaders/SVertexBuilder", "endAddVertex", "(Lbnt;)V"),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(LINESHADER, "endAddVertex", "(Lbnt;)V")));
    }

    // drawForIcon(TextureAtlasSprite,int) exists in both versions and its body binds the
    // sprite texture directly, which is what this patch redirects through the shader path.
    @MethodPatch("drawForIcon(Lbwe;I)I")
    public void drawForIcon(MethodNode method) {
        patch("bind the shader texture for MultiTexture and set the sprite bounds", method,
                remove(ByteCode.SIPush(3553)),
                ByteCode.ALoad(1),
                ByteCode.GetField("bwe", "glSpriteTextureId", "I"),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glBindTexture", "(II)V")),
                inject(ByteCode.InvokeStatic("bob", "i", "(I)V")),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic(ANISOTROPICFILTER, "bindShaderMultiTexture", "(Lbwe;)V")));
    }

    // pos(DDD) writes the four position components; the hook records the block centre
    // attribute once per vertex batch.
    @MethodPatch("a(DDD)V")
    public void putPosition(MethodNode method) {
        patch("set the at_midBlock vertex attribute for blocks", method,
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(VERTEXATTRIBUTE, "setMidBlock", "(Lbnt;)V")),
                method.instructions.getFirst());
    }
}
