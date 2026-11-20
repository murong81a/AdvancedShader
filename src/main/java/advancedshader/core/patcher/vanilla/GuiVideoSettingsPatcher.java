package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * GuiVideoSettings is bgb in 1.10.2.
 *
 * Not patched. The upstream 1.12.2 patch replaces the Config.isAnisotropicFiltering
 * check in actionPerformed so the shader options button is not hidden when anisotropic
 * filtering is enabled. In 1.10.2 the body of actionPerformed(LGuiButton;)V is a
 * different, shorter sequence (it reads the button id and defers to the parent screen)
 * and contains no isAnisotropicFiltering call at all, so there is nothing to replace.
 *
 * The consequence is only that the shader options button may be greyed out while
 * anisotropic filtering is on, which is cosmetic; the class is left otherwise untouched.
 */
@Patch("bgb")
public class GuiVideoSettingsPatcher extends Patcher {
}
