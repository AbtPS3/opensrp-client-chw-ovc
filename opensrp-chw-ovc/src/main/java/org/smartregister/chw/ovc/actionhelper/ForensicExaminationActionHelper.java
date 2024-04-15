package org.smartregister.chw.ovc.actionhelper;

import static org.smartregister.client.utils.constants.JsonFormConstants.GLOBAL;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.ovc.domain.MemberObject;
import org.smartregister.chw.ovc.domain.VisitDetail;
import org.smartregister.chw.ovc.model.BaseOvcVisitAction;
import org.smartregister.chw.ovc.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public abstract class ForensicExaminationActionHelper extends OvcVisitActionHelper {
    private MemberObject memberObject;

    private String bloodSpecimen;

    private String doesTheClientNeedLabInvestigation;

    private JSONObject jsonForm;


    public ForensicExaminationActionHelper(MemberObject memberObject) {
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        super.onJsonFormLoaded(jsonString, context, details);
        try {
            jsonForm = new JSONObject(jsonString);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Update the preprocessed form to pass client age as global parameter
     *
     * @return the updated form
     */
    @Override
    public String getPreProcessed() {
        try {
            JSONObject global = jsonForm.getJSONObject(GLOBAL);
            global.put("age", memberObject.getAge());
            global.put("gender", memberObject.getGender());
            return jsonForm.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        JSONObject payload;
        try {
            payload = new JSONObject(jsonPayload);
            bloodSpecimen = JsonFormUtils.getValue(payload, "blood_specimen");
            doesTheClientNeedLabInvestigation = JsonFormUtils.getValue(payload, "does_the_client_need_lab_investigation");
        } catch (JSONException e) {
            Timber.d(e);
        }

        processForensicExamination(doesTheClientNeedLabInvestigation);
    }

    public abstract void processForensicExamination(String doesTheClientNeedLabInvestigation);

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseOvcVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isNotBlank(bloodSpecimen)) {
            return BaseOvcVisitAction.Status.COMPLETED;
        } else
            return BaseOvcVisitAction.Status.PENDING;
    }

    public void setMemberObject(MemberObject memberObject) {
        this.memberObject = memberObject;
    }
}
