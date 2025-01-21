var paymentApi = new function() {
    var conf = $injector.paymentApi;

    function getHeaders() {
        return {
            "Content-Type": "application/json",
            "Authorization": "Basic " + $secrets.get(conf.tokens.auth, "test:test")
        };
    }

    this.getUser = function() {
        return {
            documentNumber: "12345678910",
            name: "John Sales",
            birthDate: "1992-01-01",
            email: "john@sales.com"
        };
    };

    this.updateUserInfo = function(userData) {
        // http://10.40.40.4:12500/user/update
        var response = $http.put(conf.url + conf.endpoints.updateUserInfo, {
            body: {
                documentNumber: userData.documentNumber || null,
                name: userData.name || null,
                email: userData.email || null,
                birthDate: userData.birthDate || null
            },
            headers: getHeaders()
        });
        if (response.isOk) return response.data
        return false;
    };

    this.checkEligibility = function(document) {
        var response = $http.post(conf.url + conf.endpoints.checkEligibility, {
            body: {
                documentNumber: document
            },
            headers: getHeaders()
        });
        return true;
    };

    this.getPaymentLink = function(document, pushbackLink) {
        var response = $http.post(conf.url + conf.endpoints.getPaymentLink, {
            body: {
                documentNumber: document,
                successfulPaymentPushbackLink: pushbackLink
            },
            headers: getHeaders()
        });
        return "https://example.com";
    };
}();
