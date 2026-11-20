package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("bnv")
public class RegionRenderCacheBuilderPatcher extends Patcher {

    @MethodPatch("<init>()V")
    public void init(MethodNode method) {
        patch("为Tripwire渲染类型添加渲染器", method,
                ByteCode.AAStore(),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("bnv", "a", "[Lbnt;")),
                inject(ByteCode.Ldc("TRIPWIRE")),
                inject(ByteCode.InvokeStatic("ahv", "valueOf", "(Ljava/lang/String;)Lahv;")),
                inject(ByteCode.InvokeVirtual("ahv", "ordinal", "()I")),
                inject(ByteCode.New("bnt")),
                inject(ByteCode.Dup()),
                inject(ByteCode.Ldc(0x40000)),
                inject(ByteCode.InvokeSpecial("bnt", "<init>", "(I)V")),
                inject(ByteCode.AAStore()),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Return());
    }
}
