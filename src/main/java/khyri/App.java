package khyri;

import static com.jogamp.opengl.GL4.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class App extends JFrame implements GLEventListener {

    private GLCanvas canvas;
    private int renderingProgram;
    private int[] vao = new int[1];
    
    private float rad = 0.0f;
    private float inc = 0.01f;

    public App() {
        setTitle("Chapter 2 - program 1");
        setSize(600, 400);
        setLocation(200, 200);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        this.add(canvas);
        setVisible(true);

        FPSAnimator animator = new FPSAnimator(canvas, 50);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = createShaderProgram(gl);
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    private int createShaderProgram(GL4 gl) {
        ShaderCode vertexShader = ShaderCode.create(gl, GL_VERTEX_SHADER, getClass(), "shaders", null, "vertex", "glsl", null, true);
        ShaderCode fragmentShader = ShaderCode.create(gl, GL_FRAGMENT_SHADER, getClass(), "shaders", null, "fragment", "glsl", null, true);
        
        ShaderProgram program = new ShaderProgram();
        
        program.add(vertexShader);
        program.add(fragmentShader);
        
        program.init(gl);
        
        program.link(gl, System.err);
        
        return program.program();
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glUseProgram(renderingProgram);
        
        float[] bkg = { 0.0f, 0.0f, 0.0f, 1.0f };
        FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
        gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
        
        rad += inc;
        
        if(rad > 6.28f) inc = -0.01f;
        if(rad < 0.0f) inc = 0.01f;
        
        int rads_location = gl.glGetUniformLocation(renderingProgram, "rads");
        gl.glProgramUniform1f(renderingProgram, rads_location, rad);

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }

    public static void main(String[] args) {
        new App();
    }
}
