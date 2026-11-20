package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * GameSettings is bcz in 1.10.2.
 *
 * Not patched. Both upstream patches were written against OptiFine G5's 1.12.2 body of
 * setOptionFloatValueOF, where the anisotropic filtering branch writes ofAfLevel and then
 * reloads shaders through the Minecraft field at. In H5 that method body is a long
 * per-option switch that writes option fields directly; the anisotropic branch is a
 * different, shorter sequence and the ofAfLevel write this patch anchors on does not occur
 * in it. The field does exist on the class, so the values are still set correctly; what is
 * missing is only the automatic uninit/reload when the user changes anisotropic filtering
 * while a pack is running, which a manual pack reload also achieves.
 *
 * Verified by disassembling bcz.a(GameSettings$Options, float) in both the vanilla 1.10.2
 * jar and the OptiFine-patched copy: neither body contains a putfield of ofAfLevel.
 */
@Patch("bcz")
public class GameSettingsPatcher extends Patcher {
}
