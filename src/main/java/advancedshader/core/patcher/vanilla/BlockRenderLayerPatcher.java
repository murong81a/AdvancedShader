package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * BlockRenderLayer is ahv in 1.10.2.
 *
 * The 1.10.2 enum has four constants (SOLID, CUTOUT_MIPPED, CUTOUT, TRANSLUCENT) stored
 * in the private static array field f, built in the static initialiser with an
 * iconst_4 / anewarray ahv pair followed by four putstatic blocks. Upstream 1.12.2 used
 * the same shape but with the element type written as the 1.12.2 name amm, so every
 * "amm" here is "ahv" in this version. A TRIPWIRE constant is appended at index 4 so
 * that forwards-compatible packs can name a layer the 1.10.2 enum does not have.
 */
@Patch("ahv")
public class BlockRenderLayerPatcher extends Patcher {

    @MethodPatch("<clinit>()V")
    public void clinit(MethodNode method) {
        // Grow the backing array from four entries to five.
        patch("add the TRIPWIRE render layer, array size", method,
                remove(ByteCode.IConst(4)),
                inject(ByteCode.IConst(5)),
                ByteCode.NewArray("ahv"));

        // Define the new constant and store it at index 4 before the array is put.
        patch("add the TRIPWIRE render layer, constant", method,
                inject(ByteCode.Dup()),
                inject(ByteCode.IConst(4)),
                inject(ByteCode.New("ahv")),
                inject(ByteCode.Dup()),
                inject(ByteCode.Ldc("TRIPWIRE")),
                inject(ByteCode.IConst(4)),
                inject(ByteCode.Ldc("Tripwire")),
                inject(ByteCode.InvokeSpecial("ahv", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V")),
                inject(ByteCode.AAStore()),
                ByteCode.PutStatic("ahv", "f", "[Lahv;"));
    }

    @MethodPatch("values()[Lahv;")
    public void values(MethodNode method) {
        // values() clones the private array, so the extra element is already present;
        // the hook exists because forwards-compatible packs may ask for layers the
        // 1.10.2 enum does not define, and the array is the only place that can be
        // extended without changing the enum's own constant list.
        patch("forwards-compatible render layer array", method,
                ByteCode.CheckCast("[Lahv;"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "getBlockRenderLayers", "([Lahv;)[Lahv;")),
                ByteCode.AReturn());
    }
}
