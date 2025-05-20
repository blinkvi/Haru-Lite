package cc.unknown.util.render.progress;

import java.util.Collections;
import java.util.Set;

import cc.unknown.util.structure.list.SHashSet;

public class ProgressManager {
	public static final Set<Progress> progresses = Collections.synchronizedSet(new SHashSet<>());	
	
	public static void add(Progress progress) {
        if (progresses.add(progress)) {
            progress.setPosY(progresses.size());
        }
    }

    public static void remove(Progress progress) {
        if (progresses.remove(progress)) {
            int posY = progress.getPosY();
            progress.setPosY(0);

            for (Progress p : progresses) {
                if (p.getPosY() > posY)
                    p.setPosY(p.getPosY() - 1);
            }
        }
    }
}