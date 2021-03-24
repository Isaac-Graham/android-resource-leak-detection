package cn.edu.sustech.cse.sqlab.leakdroid.pipeline.stages;

import cn.edu.sustech.cse.sqlab.leakdroid.tranformers.*;
import cn.edu.sustech.cse.sqlab.leakdroid.util.PackManagerUtil;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import soot.G;
import cn.edu.sustech.cse.sqlab.leakdroid.cmdparser.OptionsArgs;
import soot.PackManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class RunSootStage extends BaseStage {
    private final static Logger logger = Logger.getLogger(RunSootStage.class);
    protected static final String stageName = "RunSoot Stage";

    public void run() {
        G.reset();
        configureSoot();
        addTransformers();
        soot.Main.v().run(new String[]{});
    }

    @Override
    public String getStageName() {
        return stageName;
    }

    private void configureSoot() {
        soot.options.Options sootOption = soot.options.Options.v();
        sootOption.set_allow_phantom_refs(true);
        sootOption.set_ignore_resolution_errors(true);
        sootOption.set_output_dir(OptionsArgs.outputDir.getAbsolutePath());
        sootOption.set_unfriendly_mode(true);
        sootOption.set_whole_program(true);
        sootOption.set_whole_shimple(true);
        sootOption.set_verbose(OptionsArgs.isVerboseMode);
        sootOption.set_hierarchy_dirs(true);
        sootOption.set_via_shimple(true);
        sootOption.set_process_multiple_dex(true);
        sootOption.set_keep_line_number(true);


        sootOption.set_soot_classpath(String.join(File.pathSeparator, Arrays.asList(
                OptionsArgs.convertedJarFile.getAbsolutePath(),
                OptionsArgs.androidLib
                ))
        );
        sootOption.set_output_format(soot.options.Options.output_format_none);

        sootOption.set_exclude(OptionsArgs.excludedPackageNames);

        sootOption.set_process_dir(Arrays.asList(OptionsArgs.convertedJarFile.getAbsolutePath().split(File.pathSeparator)));
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
//        PackManagerUtil.addTransformation(PackManager.v(), new UnloadableBodiesEliminator());
        PackManagerUtil.addTransformation(PackManager.v(), new ICFGGenerator());
//        PackManagerUtil.addTransformation(PackManager.v(), new ICFGGenerator());
        PackManagerUtil.addTransformation(PackManager.v(), new TestICFG());
        PackManagerUtil.addTransformation(PackManager.v(), new CFGDrawer());
//        PackManagerUtil.addTransformation(PackManager.v(), new Test());
//        PackManager.v().
    }

}
