package org.ldv.melun.sio.swingpac.etudiants;

import java.awt.Color;

import org.ldv.melun.sio.swingpac.Bidule;

public class Toto extends Bidule {

  public Toto(String name) {
    super(name);
  }

  public Toto() {
    super("BOZIER");
    setBackground(Color.WHITE);
  }

  
  @Override
  public void doMove() {
    super.doMove();
  }

  @Override
  protected void doAfterImpactByOther() {
    super.doAfterImpactByOther();
    if (isGoDown())
      goOnTop();

  }

  
  
}
