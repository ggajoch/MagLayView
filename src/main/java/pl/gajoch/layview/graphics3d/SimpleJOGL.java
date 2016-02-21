package pl.gajoch.layview.graphics3d;


import java.awt.Frame;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.javafx.geom.Vec3d;
import javafx.scene.paint.Color;
import org.apache.commons.math3.geometry.euclidean.threed.CardanEulerSingularityException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.awt.DisplayMode;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;

public class SimpleJOGL implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private float rquad = 0.0f;
    private GLU glu = new GLU();
    static JFrame frame;
    static GLCanvas glcanvas;

    private Vec3d mousePos, mouseOld, mouseDelta;

    private Vec3d angle, offset;
    private double scale;


    private static final double SCROLL_SCALE = .01;
    private static final double ROTATE_SCALE = .01;
    private static final double MOVE_SCALE = .01;

    public void mouseClicked(MouseEvent e) {
        //System.out.println("Clicked");
        //System.out.println(e.getClickCount());
    }

    public void mouseEntered(MouseEvent e) {
        //System.out.println("Entered");
    }

    public void mouseExited(MouseEvent e) {
        //System.out.println("Exited");
    }

    public void mouseMoved(MouseEvent e) {
        //System.out.println("EMoved");
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        double modifier = 1.0;
        if (e.isControlDown()) {
            modifier = 0.1;
        }
        if (e.isShiftDown()) {
            modifier = 10;
        }
        scale -= SCROLL_SCALE * modifier * e.getWheelRotation();

        //cameraScale.set(Math.pow(10, scale));
    }

    public void mousePressed(MouseEvent e) {
        mousePos.set(e.getX(), e.getY(), 0);
        if ((e.getModifiers() & e.BUTTON2_MASK) != 0) {
            angle.set(0, 0, 0);
            offset.set(0, 0, 0);
            scale = 0;
        }
    }

    public void mouseReleased(MouseEvent e) {
        //System.out.println("Released");
    }

    public void mouseDragged(MouseEvent e) {
        mouseOld.set(mousePos);
        mousePos.set(e.getX(), e.getY(), 0);
        mouseDelta.set(mousePos);
        mouseDelta.sub(mouseOld);

        if (e.isControlDown()) {
            mouseDelta.x = 0;
        }

        if (e.isShiftDown()) {
            mouseDelta.y = 0;
        }

        if ((e.getModifiers() & e.BUTTON1_MASK) != 0) {
            Rotation baseRotation = new Rotation(RotationOrder.XYZ, angle.x, angle.y, angle.z);
            //get base rotation (it is transform form neutral point to actual point of view)
            Rotation deltaRotation = new Rotation(
                    new Vector3D(0, 0),
                    new Vector3D(mouseDelta.x * ROTATE_SCALE, -mouseDelta.y * ROTATE_SCALE)
            );//get rotation according to mouse movement
            Rotation finalRotation = deltaRotation.applyTo(baseRotation);//sum both the rotations
            try {
                double angles[] = finalRotation.getAngles(RotationOrder.XYZ);

                angle.x = angles[0];
                angle.y = angles[1];
                angle.z = angles[2];

            } catch (CardanEulerSingularityException ex) {
                System.out.print("ROTATE ERROR\r\n");
            }
        }
        /*if((e.getModifiers() & e.BUTTON2_MASK) != 0){
            System.out.println("Middle");
        }*/
        if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
            Rotation baseRotation = new Rotation(RotationOrder.ZXY, angle.x, angle.y, angle.z);//magic happens!
            Vector3D planeTranslate = baseRotation.applyInverseTo(
                    new Vector3D(mouseDelta.x * MOVE_SCALE, -mouseDelta.y * MOVE_SCALE, 0)
            );

            offset.x += planeTranslate.getX() * Math.pow(10, -scale);
            offset.y += planeTranslate.getY() * Math.pow(10, -scale);
            offset.z += planeTranslate.getZ() * Math.pow(10, -scale);

            //TODO: deal with window resize to maintain 1:1 movement!!
        }
    }

    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        // The canvas
        glcanvas = new GLCanvas(capabilities);
        SimpleJOGL cube = new SimpleJOGL();
        glcanvas.addGLEventListener(cube);
        glcanvas.setSize(400, 400);
        frame = new JFrame(" Multicolored cube");
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        final FPSAnimator animator = new FPSAnimator(glcanvas, 30, true);
        animator.start();

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        //update();
        render(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_NORMALIZE);

        mousePos = new Vec3d();
        mouseOld = new Vec3d();
        mouseDelta = new Vec3d();

        angle = new Vec3d();
        offset = new Vec3d();

        scale = 0;

        glcanvas.addMouseListener(this);
        glcanvas.addMouseMotionListener(this);
        glcanvas.addMouseWheelListener(this);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        if (height <= 0)
            height = 1;
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    void arrow(GL2 gl, GLUT glut, Vec3d pos, Vec3d val, Color color) {

        gl.glColor3d(color.getRed(), color.getGreen(), color.getBlue()); //green color

        gl.glPushMatrix();

        gl.glRotated(Math.toDegrees(Math.atan2(-val.y, val.z)),
                1, 0, 0);//in plane Y-Z
        gl.glRotated(Math.toDegrees(Math.atan2(val.x, Math.sqrt(Math.pow(val.y, 2) + Math.pow(val.z, 2)))),
                0, 1, 0);//out of plane Y-Z
        gl.glTranslated(pos.x, pos.y, pos.z);


        gl.glPushMatrix();
        gl.glTranslated(0, 0, val.length());
        glut.glutSolidCylinder(0.05, 0.0001, 12, 1);
        glut.glutSolidCone(0.05, 0.05, 12, 1);
        gl.glPopMatrix();

        glut.glutSolidCylinder(0.03, val.length(), 12, 1);
        gl.glPopMatrix();
    }

    private void render(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        final GLUT glut = new GLUT();
        //final GLUquadric gluq = glu.gluNewQuadric();
        //glu.gluQuadricOrientation(gluq, glu.GLU_OUTSIDE);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
        gl.glLoadIdentity();
        gl.glTranslatef(0f, 0f, -5.0f);

        gl.glRotated(Math.toDegrees(angle.x), 0, 0, 1);
        gl.glRotated(Math.toDegrees(angle.y), 1, 0, 0);
        gl.glRotated(Math.toDegrees(angle.z), 0, 1, 0);

        gl.glTranslated(offset.x, offset.y, offset.z);

        gl.glScaled(Math.pow(10, scale), Math.pow(10, scale), Math.pow(10, scale));


        arrow(gl, glut, new Vec3d(0, 0, 0), new Vec3d(0.1, 0.1, 0), Color.DARKGREEN);

        /*
        glut.glutSolidCylinder(0.05, 0.0001, 12, 1);
        glut.glutSolidCone(0.05, 0.05, 12, 1);
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -.1);
        glut.glutSolidCylinder(0.03, .1, 12, 1);
        gl.glPopMatrix();
        */
        //giving different colors to different sides
        /*gl.glBegin( GL2.GL_QUADS ); // Start Drawing The Cube
        gl.glColor3f( 1f,0f,0f );   //red color
        gl.glVertex3f( 1.0f, 1.0f, -1.0f ); // Top Right Of The Quad (Top)
        gl.glVertex3f( -1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
        gl.glVertex3f( -1.0f, 1.0f, 1.0f ); // Bottom Left Of The Quad (Top)
        gl.glVertex3f( 1.0f, 1.0f, 1.0f ); // Bottom Right Of The Quad (Top)
        */
        gl.glFlush();


        rquad -= 0.30f;
    }
}