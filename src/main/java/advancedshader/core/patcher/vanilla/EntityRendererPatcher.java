package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * EntityRenderer is bnz in 1.10.2.
 *
 * Only the prepare-program call is ported. The upstream 1.12.2 class rewrote sixteen
 * instruction sequences in renderWorldPass: it moved the cloud pass, deferred particles,
 * reordered the block damage pass, and inserted render stage markers for terrain,
 * translucent, outline and debug. Each of those anchors assumes G5's exact render order.
 * Disassembling the OptiFine-patched bnz.a(IFJ)V in H5 shows a different order: the cloud,
 * prepare and damaged-block calls those rewrites key on do not appear in this method at
 * all, so those rewrite points have no anchor here.
 *
 * What is kept: OptiFine calls Shaders.setCamera(float) once near the top of the pass to
 * set up the camera matrices, and the prepare program must run immediately after that, once
 * the matrices exist but before any geometry is submitted. That anchor is unambiguous and
 * present in both versions.
 *
 * Analysis baseline: anchors were read from tools/patched-vanilla.jar. OptiFine inserts
 * setCamera itself, so the bare vanilla jar does not contain it.
 */
@Patch("bnz")
public class EntityRendererPatcher extends Patcher {

    @MethodPatch("a(IFJ)V")
    public void renderWorldPass(MethodNode method) {
        patch("run the prepare program once the camera matrices exist", method,
                ByteCode.FLoad(2),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "setCamera", "(F)V"),
                inject(ByteCode.InvokeStatic(MORESTAGES, "renderPrepare", "()V")));
    }
}
