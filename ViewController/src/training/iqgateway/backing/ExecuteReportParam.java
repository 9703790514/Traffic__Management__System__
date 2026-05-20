package training.iqgateway.backing;

import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformConfig;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

public class ExecuteReportParam {
    final static String NAME = "Top Count";

    public static void executeReport1(String value1, String value2, String value3, String param) throws EngineException {
        IReportEngine engine = null;
        EngineConfig config = null;
        PlatformConfig platformConfig = null;
        try {
            platformConfig = new PlatformConfig();
            platformConfig.setBIRTHome("D:/birt_reports/birt-runtime-4_4_1/ReportEngine");
            config.setLogConfig("D:/birt_reports/birt-runtime-4_4_1/ReportEngine/Log", Level.FINEST);

            config = new EngineConfig();
            config.setBIRTHome("D:/birt_reports/birt-runtime-4_4_1/ReportEngine");

            Platform.startup(platformConfig);

            config = new EngineConfig();
            config.setBIRTHome("D:/birt_reports/birt-runtime-4_4_1/ReportEngine");
            config.setLogConfig("D:/birt_reports/birt-runtime-4_4_1/ReportEngine/Log", Level.FINEST);

            final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            engine = FACTORY.createReportEngine(config);

            // Open the report design
            IReportRunnable design = engine.openReportDesign(value1);

            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            task.setParameterValue(param, value3);
            task.validateParameters();

            final HTMLRenderOption HTML_OPTIONS = new HTMLRenderOption();
            HTML_OPTIONS.setOutputFileName(value2);
            HTML_OPTIONS.setOutputFormat("html");

            // For PDF output, uncomment below and comment HTML options
            // final PDFRenderOption PDF_OPTIONS = new PDFRenderOption();
            // PDF_OPTIONS.setOutputFileName(value2);
            // PDF_OPTIONS.setOutputFormat("pdf");
            // task.setRenderOption(PDF_OPTIONS);

            task.setRenderOption(HTML_OPTIONS);
            // task.setParameterValue("PRC", ""); // Example additional param
            task.run();
            task.close();
            engine.destroy();
        } catch (final Exception ex) {
            ex.printStackTrace();
        } finally {
            Platform.shutdown();
        }
    }
}
