package cn.edu.sustech.cse.sqlab.leakdroid.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.test.Test;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.CFGDrawing;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.UnloadableBodiesEliminator;
import cn.edu.sustech.cse.sqlab.leakdroid.util.PackManagerUtil;
import org.apache.log4j.Logger;
import soot.G;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.Options;
import soot.PackManager;

import java.io.File;
import java.util.Arrays;

public class RunSootStage extends BaseStage {
    private final static Logger logger = Logger.getLogger(RunSootStage.class);

    public void run() {
        G.reset();
        configureSoot();
        addTransformers();
        soot.Main.v().run(new String[]{});
    }

    private void configureSoot() {
        soot.options.Options sootOption = soot.options.Options.v();
        sootOption.set_allow_phantom_refs(true);
        sootOption.set_ignore_resolution_errors(true);
        sootOption.set_output_dir(Options.getOutputDir().getAbsolutePath());
        sootOption.set_unfriendly_mode(true);
        sootOption.set_whole_program(true);
        sootOption.set_whole_shimple(true);
        sootOption.set_verbose(Options.isVerboseMode);
        sootOption.set_hierarchy_dirs(true);
        sootOption.set_via_shimple(true);
        sootOption.set_process_multiple_dex(true);
        sootOption.set_keep_line_number(true);
        String jarFile = "C:\\Users\\Isc\\Desktop\\tmp\\AnkiDroid-rev-3e9ddc7eca.apk_d2j.jar";
        sootOption.set_process_dir(Arrays.asList(jarFile.split(File.pathSeparator)));
//        if (CommandOptions.generateSleepingApk) {
//            sootOption.set_exclude(emptyList());    // all class should be included to generate complete apk
////            sootOption.set_process_dir(sootOption.process_dir() + prepareSleeper());
//        } else if (CommandOptions.processClassInPackageIdOnly) {
//            sootOption.set_include(Arrays.asList(String.format("%s.*", CommandOptions.apkFileInfo.apkMeta.packageName)));
//        } else {
//            sootOption.set_exclude(CommandOptions.excludedPackageNames);
//        }
        sootOption.setPhaseOption("wspp", "enabled:true");
        sootOption.setPhaseOption("cg", "library:any-subtype");
        sootOption.setPhaseOption("cg", "all-reachable:true");
        sootOption.setPhaseOption("jb", "use-original-names:true");
        sootOption.setPhaseOption("cg.cha", "apponly:false");
    }

    private void addTransformers() {
        PackManagerUtil.addTransformation(PackManager.v(), new CFGDrawing());
//        PackManagerUtil.addTransformation(PackManager.v(), new UnloadableBodiesEliminator());
//        PackManagerUtil.addTransformation(PackManager.v(), new Test());
//        PackManager.v().
    }

//    private String prepareSleeper() {
//        InputStream sleeper = this.getClass().getResourceAsStream("helper/Sleeper.class");
//        try {
//            Path tempHelper = Files.createTempDirectory("").resolve("helper");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        check(tempHelper.toFile().mkdir())
//        val tempSleeper = tempHelper.resolve("Sleeper.class")
//        IOUtils.copy(sleeper, FileOutputStream(tempSleeper.toFile()))
//        return tempHelper.parent.toAbsolutePath().toString()
//    }
}
