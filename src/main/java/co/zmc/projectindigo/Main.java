package co.zmc.projectindigo;

import co.zmc.projectindigo.utils.AutoUpdater;

public class Main {

    public Main() {
        main(new String[0]);
    }

    public Main(String defaultUser) {
        main(new String[] { defaultUser });
    }

    public static void main(String[] args) {
        AutoUpdater.main(args);
    }
}
