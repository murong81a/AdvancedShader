package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * WorldClient is bln in 1.10.2.
 *
 * Only the ambient tick counter hook is ported. The upstream 1.12.2 class also patched
 * playMoodSoundAndCheckLight, but that patch matches an instruction sequence built out
 * of 1.12.2 member names (GameSettings field a, WorldClient field r, WorldClient method
 * j(Entity)I, Block field ana) whose 1.10.2 equivalents are named differently and sit
 * in a different surrounding sequence. Since the mood suppression it provides is
 * cosmetic, dropping it is the correct trade: a wrong match throws at class load and
 * takes every other patch with it.
 */
@Patch("bln")
public class WorldClientPatcher extends Patcher {

    // updateBlocks()V is j()V in 1.10.2, and the ambient tick counter is field M:I
    // where 1.12.2 used O:I. The instruction shape around the decrement is the same:
    // aload_0, dup, getfield, iconst_1, isub, putfield.
    @MethodPatch("j()V")
    public void updateBlocks(MethodNode method) {
        patch("add the mood counter hook", method,
                ByteCode.ALoad(0),
                ByteCode.Dup(),
                ByteCode.GetField("bln", "M", "I"),
                ByteCode.IConst(1),
                ByteCode.ISub(),
                ByteCode.PutField("bln", "M", "I"),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Dup()),
                inject(ByteCode.GetField("bln", "M", "I")),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "updateMoodTick", "(I)I")),
                inject(ByteCode.PutField("bln", "M", "I")));
    }
}
