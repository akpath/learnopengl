package khyri;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

import java.nio.FloatBuffer;

import javax.swing.JFrame;

import org.joml.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

public class App extends JFrame implements GLEventListener {

    private GLCanvas canvas;

    private int renderingProgram;

    private int[] vao = new int[1];
    private int[] vbo = new int[2];

    private float cameraX, cameraY, cameraZ;
    private float cubeX, cubeY, cubeZ;
    private Matrix4f pMat;

    public App() {
        setTitle("Chapter 4 - program 1");
        setSize(600, 600);
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        this.add(canvas);
        setVisible(true);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = createShaderProgram(gl);

        setupVertices();

        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 8.0f;
        cubeX = 0.0f;
        cubeY = -2.0f;
        cubeZ = 0.0f;

        float aspect = (float) canvas.getWidth() / (float) canvas.getHeight();
        pMat = new Matrix4f().perspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
    }

    private void setupVertices() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        float[] vertices = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
                -1.0f };
        
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(vertices);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);
    }

    private int createShaderProgram(GL4 gl) {
        ShaderCode vertexShader = ShaderCode.create(gl, GL_VERTEX_SHADER, getClass(), "shaders", null, "vertex", "glsl",
                null, true);
        ShaderCode fragmentShader = ShaderCode.create(gl, GL_FRAGMENT_SHADER, getClass(), "shaders", null, "fragment",
                "glsl", null, true);

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
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        Matrix4f mvMat = new Matrix4f().translate(-cameraX, -cameraY, -cameraZ).translate(cubeX, cubeY, cubeZ);

        int mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
        int projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix");
        FloatBuffer floatBuffer = Buffers.newDirectFloatBuffer(16);
        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(floatBuffer));
        gl.glUniformMatrix4fv(projLoc, 1, false, pMat.get(floatBuffer));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
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
