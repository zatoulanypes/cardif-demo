theme: /Proposal

    state: CheckProductStatus
        script:
            $analytics.setSessionResult("Product proposal");
            $session.user = paymentApi.getUser();
        if: !$temp.entryState.startsWith("/Objection")
            script:
                moment.locale("en");
                $temp.insuranceExpirationDate = moment().subtract(3, "days").format("D MMMM YYYY");
            a: So, It looks like your insurance ran out on {{ $temp.insuranceExpirationDate }}. 🕒
            a: Renew it to keep your home protected from:
                🔥 fire,
                ⚡ lightning strike and electrical damage,
                🕵️‍♂️ theft.

                And a lot of services included:
                👨‍🔧 plumber,
                🔌 electrician,
                🚪 glazier.

                So, are you interested in renewing your policy?
        else:
            a: So, do you want to renew your insurance policy? 🤔
        buttons:
            "Renew" -> /PersonalInfo/PersonalInfoConfirmation
        q: ($localAgree/renew) || toState = "/PersonalInfo/PersonalInfoConfirmation"
        q: $localDisagree || toState = "/Objection/NotInterested"
