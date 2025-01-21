/* eslint-disable consistent-return, max-len */

function countRepeatsInRow() {
    $.temp.entryState = $.currentState;
    if (_.contains([$.session.lastStateWithQuestions, $.session.lastEntryState], $.currentState)) {
        $.session.repeatsInRow += 1;
    } else {
        $.session.repeatsInRow = 1;
    }
    return $.session.repeatsInRow;
}

function counter(param) {
    var key = param || $.currentState;
    $.session.counter = $.session.counter || {};
    $.session.counter[key] = $.session.counter[key] || 0;
    $.session.counter[key] += 1;
    return $.session.counter[key];
}

function anyErrorProcessing() {
    $analytics.setSessionResult("There's been an error in the bot");
    $analytics.setSessionData("ERROR", "YES");
    $reactions.answer("I apologise, there seems to be a technical issue. I have reported it to our specialists, and they are working on a solution. 🔧");
    $reactions.answer("In that case, I suggest you contact your nearest a remote sales agent to continue discussing your insurance policy. You can find one via this [site](https://example.com).");
    if ($.exception && $.exception.message) {
        var exceptionMessage = $.exception.message;
        log("errorProcessing exceptionMessage: " + exceptionMessage);
    }
    $jsapi.stopSession();
}

function validateDocument(number, length) {
    return number.length === length ? number : undefined;
}

function validateDate(date) {
    var currentDate = moment($jsapi.currentTime());
    return moment(date).isBefore(currentDate);
}

function formatDate(date, pattern) {
    return moment(date).format(pattern || "YYYY-MM-DD");
}

function concatFullAddress(user) {
    var address = _.pick(user, "country", "state", "city", "neighborhood", "street", "number", "complement", "postalCode");
    return address.street + ", "
        + address.number
        + (address.complement ? " " + address.complement : "") + ", "
        + address.neighborhood + ", "
        + address.city + " - "
        + address.state + ", "
        + "CEP" + address.postalCode;
}

function capitalizeEveryToken(inputString) {
    var tokens = inputString.split(" ");
    var capitTokens = tokens.map(function(token) {
        return capitalize(token);
    });
    return capitTokens.join(" ");
}

function parsePhoneNumber(request) {
    if (request.channeltype !== "whatsapp") return;
    if (!request.rawRequest.from || !request.rawRequest.from.startsWith("55")) return;
    var ddd = phoneString.slice(2, 4);
    var localNumber = phoneString.slice(4);
    if (!ddd || !localNumber || localNumber.length !== 8 || localNumber.length !== 9) return;
    return {
        ddd: ddd,
        localNumber: localNumber
    };
}

function getPushbackLink(eventName) {
    var pushback = $pushgate.createPushback(
        $.request.channelType,
        $.request.botId,
        $.request.channelUserId,
        eventName,
        {}
    );
    return "https://platform.platform-fr.tovie.ai/pushgate/push_" + pushback.id;
}
