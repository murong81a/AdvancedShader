package advancedshader.core.patcher.vanilla;

import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("bob")
public class GlStateManagerPatcher extends Patcher {

    @ClassPatch
    public void patch(ClassNode clazz) {
        MethodVisitor m = clazz.visitMethod(ByteCode.ACC_PUBLIC | ByteCode.ACC_STATIC, "applyCurrentBlend", "()V", null, null);

        m.visitCode();

        Label disable = new Label();
        Label checkEnd = new Label();

        m.visitIntInsn(ByteCode.SIPUSH, GL11.GL_BLEND);
        m.visitFieldInsn(ByteCode.GETSTATIC, "bob", "g", "Lbob$b;");
        m.visitFieldInsn(ByteCode.GETFIELD, "bob$b", "a", "Lbob$c;");
        m.visitFieldInsn(ByteCode.GETFIELD, "bob$c", "b", "Z");
        m.visitJumpInsn(ByteCode.IFEQ, disable);
        m.visitMethodInsn(ByteCode.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnable", "(I)V", false);
        m.visitJumpInsn(ByteCode.GOTO, checkEnd);
        m.visitLabel(disable);
        m.visitMethodInsn(ByteCode.INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisable", "(I)V", false);
        m.visitLabel(checkEnd);

        m.visitFieldInsn(ByteCode.GETSTATIC, "bob", "g", "Lbob$b;");
        m.visitFieldInsn(ByteCode.GETFIELD, "bob$b", "b", "I");
        m.visitFieldInsn(ByteCode.GETSTATIC, "bob", "g", "Lbob$b;");
        m.visitFieldInsn(ByteCode.GETFIELD, "bob$b", "c", "I");
        m.visitFieldInsn(ByteCode.GETSTATIC, "bob", "g", "Lbob$b;");
        m.visitFieldInsn(ByteCode.GETFIELD, "bob$b", "d", "I");
        m.visitFieldInsn(ByteCode.GETSTATIC, "bob", "g", "Lbob$b;");
        m.visitFieldInsn(ByteCode.GETFIELD, "bob$b", "e", "I");
        m.visitMethodInsn(ByteCode.INVOKESTATIC, "caf", "c", "(IIII)V", false);

        m.visitInsn(ByteCode.RETURN);

        m.visitEnd();
    }
}
