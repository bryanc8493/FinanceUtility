package com.bryan.finance.utilities;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.literals.ApplicationLiterals;

import java.io.File;

public class DateUtil extends ApplicationLiterals {

    public static String getDeploymentDate() {
        String productionDirectory = ReadConfig.getConfigValue(DEPLOYMENT_LOCATION);
        File productionArtifact = new File(productionDirectory + SLASH + APP_ARTIFACT + DOT_JAR);

        long modifiedDate = productionArtifact.lastModified();
        return MONTH_DAY_YEAR.format(modifiedDate);
    }
}
