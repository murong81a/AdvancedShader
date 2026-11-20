package advancedshader.core.patcher.optifine;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

/**
 * net.optifine.shaders.gui.GuiShaders, not patched.
 *
 * Two upstream patches targeted this class: one appended buttons for the forward
 * compatible options in updateButtons, the other forwarded button clicks in
 * actionPerformed. Both were written against G5's layout.
 *
 * In H5 the actionPerformed body starts by testing GuiButton.enabled and instanceof
 * GuiButtonEnumShaderOption, then dispatches on the button id through a table switch,
 * and updateButtons reads the inherited public buttonList rather than the private field
 * the 1.12.2 patch expects. Neither anchor survives translation, and both patches only
 * affect the shader selection screen: the options they add can be set in
 * optionsshaders.txt, and the screen itself works without them.
 *
 * This class is therefore left alone rather than given a guessed-at rewrite. Note that the
 * hooks it would call (ForwardFeatures.actionPerformed and getButtonText) still exist and
 * are still used by GuiButtonEnumShaderOptionPatcher, so nothing is left dangling.
 */
@Patch("net.optifine.shaders.gui.GuiShaders")
public class GuiShadersPatcher extends Patcher {

    // No method patches on purpose; see the class comment.
    @SuppressWarnings("unused")
    private static void unusedMarker() {
    }
}
