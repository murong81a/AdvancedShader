package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * ChunkRenderContainer is bnw in 1.10.2.
 *
 * This patch feeds the chunk offset uniform, so a shader knows where the chunk being drawn
 * sits relative to the camera. It reads the chunk position, subtracts the view entity
 * position, and passes the result to the hook.
 *
 * The 1.12.2 original read that position through RenderChunk.k(), which returned a
 * Vec3i in that version. Two things differ here and both produced a real defect when
 * translated naively:
 *
 *   * 1.10.2's RenderChunk.k() returns BlockPos (cm), not Vec3i;
 *   * the coordinate getters are declared on Vec3i (di), so the owner in the invoke must
 *     be di even though the value on the stack is a BlockPos.
 *
 * Getting this wrong produced `NoSuchMethodError: RenderChunk.k()...INetHandler`, thrown
 * from ChunkRenderContainer on the first chunk drawn. The visible symptom was a world
 * showing only sky and clouds with the game appearing to hang, which reads like a shader
 * fault and was in fact a single wrong descriptor.
 *
 * Field names c, d and e are viewEntityX, viewEntityY and viewEntityZ in 1.10.2, the same
 * positions the 1.12.2 code used.
 */
@Patch("bnw")
public class ChunkRenderContainerPatcher extends Patcher {

    @MethodPatch("a(Lbqy;)V")
    public void preRenderChunk(MethodNode method) {
        LabelNode label = ByteCode.Label();

        patch("supply the chunk offset uniform", method,
                inject(ByteCode.Label()),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeVirtual("bqy", "k", "()Lcm;")),
                inject(ByteCode.InvokeVirtual("di", "p", "()I")),
                inject(ByteCode.I2D()),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("bnw", "c", "D")),
                inject(ByteCode.DSub()),
                inject(ByteCode.D2F()),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeVirtual("bqy", "k", "()Lcm;")),
                inject(ByteCode.InvokeVirtual("di", "q", "()I")),
                inject(ByteCode.I2D()),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("bnw", "d", "D")),
                inject(ByteCode.DSub()),
                inject(ByteCode.D2F()),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeVirtual("bqy", "k", "()Lcm;")),
                inject(ByteCode.InvokeVirtual("di", "r", "()I")),
                inject(ByteCode.I2D()),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("bnw", "e", "D")),
                inject(ByteCode.DSub()),
                inject(ByteCode.D2F()),
                inject(ByteCode.InvokeStatic(CHUNKOFFSET, "setChunkOffset", "(FFF)Z")),
                inject(ByteCode.IfZero(label)),
                inject(ByteCode.Return()),
                inject(label),
                method.instructions.getFirst());
    }
}
