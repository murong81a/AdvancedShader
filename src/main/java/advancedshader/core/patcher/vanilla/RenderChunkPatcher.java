package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * RenderChunk is bqy in 1.10.2, the same official name it has in 1.12.2.
 *
 * The upstream 1.12.2 patcher also patched `fixBlockLayer(LeafBlockState, BlockRenderLayer)`,
 * a method that does not exist in 1.10.2 at all: the tripwire render layer was converted in
 * that version's BlockRenderLayer enum handling instead. That patch is therefore not ported,
 * which is why this class carries only the modelview patch.
 */
@Patch("bqy")
public class RenderChunkPatcher extends Patcher {

    // modelviewMatrix is field k in both versions, and GlStateManager.multMatrix is a(FloatBuffer)V,
    // so the instruction sequence this matches is identical to the upstream 1.12.2 one.
    @MethodPatch("g()V")
    public void multModelviewMatrix(MethodNode method) {
        patch("remove the unexplained chunk scale (may affect shadow rays)", method,
                remove(ByteCode.ALoad(0)),
                remove(ByteCode.GetField("bqy", "k", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.InvokeStatic("bob", "a", "(Ljava/nio/FloatBuffer;)V")));
    }
}
