package com.bryan.finance.utilities;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.literals.ApplicationLiterals;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import java.io.File;
import java.util.Date;
import java.util.Properties;

public class DateUtil extends ApplicationLiterals {

    public static String getDeploymentDate() {
        String productionDirectory = ReadConfig.getConfigValue(DEPLOYMENT_LOCATION);
        File productionArtifact = new File(productionDirectory + SLASH + APP_ARTIFACT + DOT_JAR);

        long modifiedDate = productionArtifact.lastModified();
        return MONTH_DAY_YEAR.format(modifiedDate);
    }

    public static JDatePickerImpl getDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        final JDatePickerImpl datePicker = new JDatePickerImpl(datePanel,
                new DateLabelFormatter());
        model.setValue(new Date());
        model.setSelected(true);
        return datePicker;
    }
}
