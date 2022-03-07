package com.jpexs.games.rpsls.view;

import java.awt.event.ActionListener;

/**
 *
 * @author JPEXS
 */
public interface IRpslView {

    public void addSetFlagListener(SetPointListener listener);

    public void removeSetFlagListener(SetPointListener listener);

    public void addSetTrapListener(SetPointListener listener);

    public void removeSetTrapListener(SetPointListener listener);

    public void addSetChooserListener(SetPointListener listener);

    public void removeSetChooserListener(SetPointListener listener);

    public void addStartListener(ActionListener listener);

    public void removeStartListener(ActionListener listener);

    public void addShuffleListener(ActionListener listener);

    public void removeShuffleListener(ActionListener listener);

    public void addMoveListener(MoveListener listener);

    public void removeMoveListener(MoveListener listener);

    public void addProceedListener(ActionListener listener);

    public void removeProceedListener(ActionListener listener);

    public void addSelectWeaponListener(SelectWeaponListener listener);

    public void removeSelectWeaponListener(SelectWeaponListener listener);

    public void initView();

    public int getTeam();

}
