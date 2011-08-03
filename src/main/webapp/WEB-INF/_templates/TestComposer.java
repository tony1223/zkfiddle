import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class ${class-name} extends GenericForwardComposer{

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

	}

	public void onClick$btn(Event e){
		try {
			Messagebox.show("Hi btn");
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
}
