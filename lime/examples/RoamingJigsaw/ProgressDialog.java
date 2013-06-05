
import java.awt.*;
import javax.swing.*;

public class ProgressDialog extends JDialog {
  public static void main(String[] args) {
    ProgressDialog pd = new ProgressDialog(null,
        "ProgressDialog Test", false, 10);
    pd.setVisible(true);

    for(int i = 1; i <= 10; i++) {
      try { Thread.sleep(700); }
      catch(InterruptedException iex) { }
      pd.setValue(i);
    }

    pd.setVisible(false);
    pd.dispose();
    System.exit(0);
  }

  private JProgressBar bar;
  private int max;

  public ProgressDialog(Frame owner, String title, boolean modal, int max) {
    super(owner, title, modal);
    this.max = max;

    bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, max);
    bar.setPreferredSize(new Dimension(bar.getPreferredSize().width * 2,
                                       bar.getPreferredSize().height * 2));
    getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
    getContentPane().add(bar);
    pack();
    if(owner == null) {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation(screenSize.width/2 - getWidth()/2,
                  screenSize.height/2 - getHeight()/2);
    }
    else {
      setLocation(owner.getX() + owner.getWidth()/2 - getWidth()/2,
                  owner.getY() + owner.getHeight()/2 - getHeight()/2);
    }
  }

  public void setValue(int value) {
    bar.setValue(value);
  }
}
