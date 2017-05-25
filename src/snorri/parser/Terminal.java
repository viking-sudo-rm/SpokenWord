package snorri.parser;

import snorri.events.SpellEvent;
import snorri.semantics.Category;

public class Terminal<S> implements Node<S> {

	private static final long serialVersionUID = 1L;
	private String orthography;
	
	public Terminal(String orthography) {
		this.orthography = orthography;
	}
	
	public boolean equals(Terminal<?> other) {
		return orthography.equals(other.orthography);
	}
	
	public boolean equals(String other) {
		return orthography.equals(other);
	}
	
	public String getOrthography() {
		return orthography;
	}
	
	@Override @SuppressWarnings("unchecked")
	public S getMeaning(SpellEvent e) {
		if (Lexicon.lookup(orthography) != null)
			return (S) Lexicon.lookup(orthography).getMeaning(e);
		return null;
	}
	
	public String toString() {
		return "`" + orthography + "`";
	}

	@Override
	public boolean altersMovement() {
		
		if (Lexicon.lookup(orthography) == null) {
			return false;
		}
		
		return Lexicon.lookup(orthography).altersMovement();
	}

	@Override
	public Terminal<S> copy() {
		return new Terminal<>(orthography);
	}

	@Override
	public Category getCategory() {
		//FIXME
		return null;
	}
	
}
