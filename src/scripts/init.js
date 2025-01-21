$global.CATCH_ALL_LIMIT = $injector.catchAllLimit;

$global.$ = {
    __noSuchProperty__: function(property) {
        return property === "context" ? $jsapi.context() : $jsapi.context()[property];
    }
};

bind("selectNLUResult", function($context) {
    var allResults = _.chain($context.nluResults)
        .omit("selected")
        .values()
        .flatten()
        .value();
    var maxScore = _.chain(allResults)
        .pluck("score")
        .max()
        .value();
    $context.nluResults.selected = _.findWhere(allResults, {
        score: maxScore
    });
});

bind("preProcess", function() {
    $.temp.entryState = $.currentState;
});

bind("postProcess", function() {
    if (!_.contains(["/Objection/NotInterested"], $.contextPath)) $.session.lastContextState = $.contextPath;
    if (!$.currentState.endsWith("CatchAll")) $.session.lastState = $.currentState;
    $.session.lastStateWithQuestions = $.currentState;
    $.session.lastEntryState = $.temp.entryState;
    log("ParseTree: " + toPrettyString($.parseTree));
});

bind("onAnyError", function() {
    $analytics.setSessionResult("There's been an error in the bot");
    anyErrorProcessing();
});

bind("onScriptError", function() {
    if ($.exception.message.startsWith("Error calling updateUserInfo")) {
        $reactions.transition("/PersonalInfo/UserScreening/UpdateUserInfoError");
    } else if ($.exception.message.startsWith("Error calling checkEligibility")) {
        $analytics.setSessionResult("Error checkng user eligibility");
        $reactions.transition("/PersonalInfo/UserScreening/CheckEligibilityError");
    } else anyErrorProcessing();
}, "/PersonalInfo/UserScreening", "User screening ERROR");

bind("onScriptError", function() {
    $reactions.transition("/Payment/GenerateLink/LocalApiError");
}, "/Payment/GenerateLink", "Payment link generation ERROR");
