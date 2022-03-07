package com.jpexs.games.rpsls;

import com.jpexs.games.rpsls.model.Point;
import com.jpexs.games.rpsls.model.RpslsModel;
import com.jpexs.games.rpsls.model.Weapon;
import com.jpexs.games.rpsls.view.IRpslView;
import com.jpexs.games.rpsls.view.MoveListener;
import com.jpexs.games.rpsls.view.SelectWeaponListener;
import com.jpexs.games.rpsls.view.SetPointListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class RpslsController {

    private RpslsModel model;
    private List<IRpslView> views;
    private boolean started = false;

    public RpslsController(RpslsModel model) {
        this.model = model;
        this.views = new ArrayList<>();
    }

    public void addView(IRpslView view) {
        if (started) {
            throw new RuntimeException("Game alread started, cannot add view");
        }
        views.add(view);

        view.addSetFlagListener(new SetPointListener() {
            @Override
            public void setPoint(Point point) {
                model.setFlagPosition(view.getTeam(), point);
            }
        }
        );

        view.addSetTrapListener(new SetPointListener() {
            @Override
            public void setPoint(Point point) {
                model.setTrapPosition(view.getTeam(), point);
            }
        }
        );

        view.addSetChooserListener(new SetPointListener() {
            @Override
            public void setPoint(Point point) {
                model.setChooserPosition(view.getTeam(), point);
            }
        }
        );

        view.addMoveListener(new MoveListener() {
            @Override
            public void move(Point source, Point destination) {
                model.move(view.getTeam(), source, destination);
            }
        });

        view.addStartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.beginPlay(view.getTeam());
            }
        });

        view.addShuffleListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.shuffleWeapons(view.getTeam());
            }
        });

        view.addSelectWeaponListener(new SelectWeaponListener() {
            @Override
            public void setWeapon(Weapon weapon) {
                model.selectDuelWeapon(view.getTeam(), weapon);
            }
        });

        view.addProceedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.proceed(view.getTeam());
            }
        });
    }

    public void start() {
        started = true;
        for (IRpslView view : views) {
            view.initView();
        }
    }
}
