package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("boo")
public class VboRenderListPatcher extends Patcher {

    @MethodPatch("preRenderRegion(III)V")
    public void preRenderRegion(MethodNode method) {
        LabelNode label = ByteCode.Label();

        patch("添加chunkOffset Uniform变量", method,
                inject(ByteCode.Label()),
                inject(ByteCode.ILoad(1)),
                inject(ByteCode.I2D()),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("boo", "viewEntityX", "D")),
                inject(ByteCode.DSub()),
                inject(ByteCode.D2F()),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.I2D()),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("boo", "viewEntityY", "D")),
                inject(ByteCode.DSub()),
                inject(ByteCode.D2F()),
                inject(ByteCode.ILoad(3)),
                inject(ByteCode.I2D()),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("boo", "viewEntityZ", "D")),
                inject(ByteCode.DSub()),
                inject(ByteCode.D2F()),
                inject(ByteCode.InvokeStatic(CHUNKOFFSET, "setChunkOffset", "(FFF)Z")),
                inject(ByteCode.IfZero(label)),
                inject(ByteCode.Return()),
                inject(label),
                method.instructions.getFirst());
    }
}
