package cn.edu.sustech.cse.sqlab.leakdroid.runSoot;

import cn.edu.sustech.cse.sqlab.leakdroid.Main;
import org.apache.log4j.Logger;
import soot.G;
import soot.options.Options;
import cn.edu.sustech.cse.sqlab.leakdroid.CommandOptions;
import soot.PackManager;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.axml.ApkHandler;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

public class SootAnalyzer {
    private static Logger logger = Logger.getLogger(SootAnalyzer.class);

    public void run() {
        G.reset();
        configureSoot();
        addTransformers();
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


    }

    private void addTransformers() {

    }
}
