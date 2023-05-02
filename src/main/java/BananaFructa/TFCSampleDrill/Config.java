package BananaFructa.TFCSampleDrill;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.List;

public class Config {

    public static Configuration config;

    public static boolean hideIEMinerals = true;

    public static void load(File configDirectory) {
        config = new Configuration(new File(configDirectory, "tfcsampledrill.cfg"));
        hideIEMinerals = config.getBoolean("hide_IE_minerals","general",true,"Set to false if you also want to see Immersive Engineering's mineral info.");
        if (config.hasChanged()) config.save();
    }

}
