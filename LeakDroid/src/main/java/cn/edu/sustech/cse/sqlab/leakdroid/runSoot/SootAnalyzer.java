package cn.edu.sustech.cse.sqlab.leakdroid.runSoot;

import cn.edu.sustech.cse.sqlab.leakdroid.Main;
import cn.edu.sustech.cse.sqlab.leakdroid.test.Test;
import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.UnloadableBodiesEliminator;
import cn.edu.sustech.cse.sqlab.leakdroid.util.PackManagerUtil;
import org.apache.log4j.Logger;
import soot.G;
import soot.options.Options;
import cn.edu.sustech.cse.sqlab.leakdroid.CommandOptions;
import soot.PackManager;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.axml.ApkHandler;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public class SootAnalyzer {
    private static Logger logger = Logger.getLogger(SootAnalyzer.class);

    public void run() {
        G.reset();
        configureSoot();
        addTransformers();
        soot.Main.v().run(new String[]{});
    }

    private void configureSoot() {
        Options sootOption = soot.options.Options.v();
        sootOption.set_allow_phantom_refs(true);
        sootOption.set_ignore_resolution_errors(true);
        sootOption.set_output_dir(CommandOptions.outputDir.getAbsolutePath());
        sootOption.set_unfriendly_mode(true);
        sootOption.set_whole_program(true);
        sootOption.set_whole_shimple(true);
        sootOption.set_verbose(CommandOptions.isVerboseMode);
        sootOption.set_hierarchy_dirs(true);
        sootOption.set_via_shimple(true);
        sootOption.set_process_multiple_dex(true);
        sootOption.set_keep_line_number(true);
        String apkFile = "C:\\Users\\Isc\\Desktop\\github-benchmark_d2j.jar";
        sootOption.set_process_dir(Arrays.asList(apkFile.split(File.pathSeparator)));
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
        PackManagerUtil.addTransformation(PackManager.v(), new UnloadableBodiesEliminator());
        PackManagerUtil.addTransformation(PackManager.v(), new Test());
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