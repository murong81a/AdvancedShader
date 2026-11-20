package advancedshader.core.patcher.optifine;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * net.optifine.shaders.gui.GuiSlotShaders.
 *
 * checkCompatible decides whether a pack shows as compatible by reading
 * "version.<game version>" and "version.<OptiFine release>" from the pack's
 * shaders.properties.
 *
 * The upstream 1.12.2 patch replaced the literal "version.1.12.2" so that a pack declaring
 * compatibility with a newer game version is still accepted. In H5 that literal is
 * already "version.1.10.2", because H5 is the build made for this game version, so the
 * literal lookup fails and must be retargeted to the string this version actually contains.
 * The rewrite keeps the meaning: ask the hook what version key to probe instead of the
 * hard-coded one.
 *
 * The second upstream patch anchors on the OptiFine release comparison, which in H5 reads
 * the literal "H5". That comparison is left alone: it is already correct for this build,
 * and the upstream patch existed to make a G5 build report itself as a newer release, which
 * does not apply here.
 */
@Patch("net.optifine.shaders.gui.GuiSlotShaders")
public class GuiSlotShadersPatcher extends Patcher {

    @MethodPatch("checkCompatible(Lnet/optifine/shaders/IShaderPack;I)Z")
    public void checkCompatible(MethodNode method) {
        patch("ask the hook which game version key to test", method,
                ByteCode.Ldc("version.1.10.2"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "getGameVersionKey",
                        "(Ljava/lang/String;)Ljava/lang/String;")));
    }
}
