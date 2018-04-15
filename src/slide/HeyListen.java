/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author stark
 */
public class HeyListen {
    JFrame frame;
    Slide sl;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    public HeyListen(Slide sl,JFrame frame){
        this.sl = sl;
        this.frame=frame;
    }
    
    boolean debug=true;
    String buildLabel(String plus) {
        if(debug){
      return "<html>"
              + plus+"<br><br>"
              + "R: recurse<br>"
              + "DOWN: skip -10<br>"
              + "LEFT: skip -1<br>"
              + "UP: skip+10<br>"
              + "RIGHT: skip +1<br>"
              + "T: timer<br>"
              + "L: load<br>"
              + "F3: save prefs<br>"
              + "F2: load prefs<br>"
              + "H: toggle debug<br>"
              + "ESC: quit</html>";
        }
        return "";
    }
    
    public KeyListener getShortcutKeyListener() {
        KeyListener listener = new KeyListener() {

            @Override
            public void keyReleased(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_R:
                        sl.recur();
                        break;
                    case KeyEvent.VK_DOWN:
                        sl.skip(-10);
                        break;
                    case KeyEvent.VK_LEFT:
                        sl.skip(-2);
                        break;
                    case KeyEvent.VK_UP:
                        sl.skip(10);
                        break;
                    case KeyEvent.VK_RIGHT:
                        sl.skip(1);
                        break;
                    case KeyEvent.VK_T:
                        sl.popTimer();
                        break;
                    case KeyEvent.VK_S:
                        sl.sliderun();
                        break;
                    case KeyEvent.VK_L:
                        sl.setImg();
                        break;
                    case KeyEvent.VK_H:
                        debug=!debug;
                        sl.setDebug();
                        break;
                    case KeyEvent.VK_F3:
                        System.out.println("F3");
                        sl.saveProps();
                        break;
                    case KeyEvent.VK_F2:
                        System.out.println("F2");
                        sl.getProps();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        exiton(frame);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Do nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Do nothing
            }
        };
        return listener;
    }
    
    public void exiton(Container dialog) {
        System.out.println("exit");
        sl.exiton();
        
//            Container frame1 = btnExit.getParent();
//            do {
//                frame1 = frame1.getParent();
//            } while (!(frame1 instanceof JFrame));
//            ((JFrame) frame1).dispose();
            
//        Container frame1 = dialog;
//        do {
//            frame1 = dialog.getParent();
//        } while (!(frame1 instanceof JFrame));
//        ((JFrame) frame1).dispose();
    }
    
    public List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }

    

}
