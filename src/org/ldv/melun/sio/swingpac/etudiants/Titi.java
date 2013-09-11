package org.ldv.melun.sio.swingpac.etudiants;

import java.awt.Color;

import org.ldv.melun.sio.swingpac.Bidule;

public class Titi extends Bidule {

  private int nbDeplacements;

  public Titi() {
    super("FAISAL");
    setBackground(Color.YELLOW);
    nbDeplacements = 0;
  }

  @Override
  public void doMove() {
    nbDeplacements++;
    super.doMove();
    // tous les 200 deplacements et si descente
    if (isGoDown() && nbDeplacements % 200 == 0) 
      if (isGoLeft())
        goOnRight();
      else
        goOnLeft();
  }

}
