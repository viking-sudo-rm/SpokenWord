package snorri.semantics;

import snorri.events.SpellEvent;
import snorri.parser.Node;
import snorri.triggers.Trigger.TriggerType;

public class Pray extends TransVerbDef {

	public Pray() {
		super();
	}

	@Override
	public boolean exec(Node<Object> object, SpellEvent e) {
		Object obj = object.getMeaning(e);
		if (obj == null) {
			return false;
		}
		TriggerType.PRAY.activate(obj.toString());
		return true;
	}

	@Override
	public boolean eval(Object subj, Object obj, SpellEvent e) {
		return e.getWorld().getTriggerMap().contains(TriggerType.PRAY, obj.toString());
	}

	@Override
	public String toString() {
		return "pray to (name)";
	}

}