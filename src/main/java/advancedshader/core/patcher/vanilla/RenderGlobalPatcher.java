package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * RenderGlobal is boh in 1.10.2.
 *
 * The two gates below exclude entities and block entities from the shadow pass. They are
 * written without new branch targets on purpose.
 *
 * Why that matters: the natural way to write a gate is
 *
 *     if (!inShadowPass || config.isTrue()) { original code }
 *
 * which inserts jumps and therefore requires the class writer to recompute stack frames.
 * ASM 5's frame computation is pathological on very large methods, and RenderGlobal's
 * entity pass is one of the largest in the game: recomputing its frames exhausted the heap
 * even at a six gigabyte heap ("GC overhead limit exceeded" inside Frame.b), and because
 * frame computation runs when the class is written, that one failure discarded every patch
 * on the class, including patches that had already applied cleanly. The visible symptom was
 * a world with sky and clouds but no terrain.
 *
 * The gates are therefore expressed as a straight-line call that mutates Shaders state and
 * returns void, with no jump at all. The shadow pass then reads that state through the
 * existing OptiFine calls it already makes. This keeps the class writer on COMPUTE_MAXS,
 * which never touches frames, so the patch cannot fail this way.
 *
 * The anchor is the iload 21 / ifeq / invokestatic Shaders.endEntities triple that OptiFine
 * itself emits, read from tools/patched-vanilla.jar rather than the bare vanilla jar.
 */
@Patch("boh")
public class RenderGlobalPatcher extends Patcher {

    @MethodPatch("a(Lrw;Lbrf;F)V")
    public void renderEntities(MethodNode method) {
        // Before beginEntities: tell the hook which pass is starting. The hook decides
        // internally whether the shadow-pass config allows entities, so no branch is added.
        patch("mark the entity pass start for the shadow config", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "beginEntities", "()V"),
                inject(ByteCode.InvokeStatic(HOOK, "noteEntityPassStart", "()V")));

        // Before endEntities: close the pass symmetrically.
        patch("mark the entity pass end", method,
                ByteCode.ILoad(21),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "endEntities", "()V"),
                inject(ByteCode.InvokeStatic(HOOK, "noteEntityPassEnd", "()V")));

        // Before beginBlockEntities: the profiler section OptiFine opens carries the name.
        patch("mark the block entity pass start", method,
                ByteCode.Ldc("blockentities"),
                ByteCode.InvokeVirtual("oq", "c", "(Ljava/lang/String;)V"),
                inject(ByteCode.InvokeStatic(HOOK, "noteBlockEntityPassStart", "()V")));

        // Before endBlockEntities, same guard shape as the entity pass.
        patch("mark the block entity pass end", method,
                ByteCode.ILoad(21),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "endBlockEntities", "()V"),
                inject(ByteCode.InvokeStatic(HOOK, "noteBlockEntityPassEnd", "()V")));
    }
}
