theme: /PersonalInfo

    state: PersonalInfoConfirmation
        script:
            $analytics.setSessionResult("Confirm personal info");
        if: counter() === 1
            a: Great! Now I need to confirm some of your personal data 🙏
        if: !$session.user.documentNumber && $session.isAuthByPhoneNumber
            go!: /PersonalInfo/RequestDocument
        if: !$session.user.name
            go!: /PersonalInfo/RequestName
        if: !$session.user.birthDate
            go!: /PersonalInfo/RequestDob
        if: !$session.user.email
            go!: /PersonalInfo/ReuqestEmail
        script:
            moment.locale("pt");
            $temp.birthDate = moment($session.user.birthDate).format("D MMMM YYYY");
        a: Is everything correct here? 📝

            Document number: {{ $session.user.documentNumber }}
            Full name: {{ $session.user.name }}
            Date of birth: {{ $temp.birthDate }}
            Email: {{ $session.user.email }}
        buttons:
            "Correct" -> /PersonalInfo/UserScreening
            "Incorrect" -> ./Incorrect
        q: ($correct/$localAgree) || toState = "/PersonalInfo/UserScreening"
        q: * {$incorrect * number} * || toState = "/PersonalInfo/RequestDocument"
        q: * {$incorrect * *name} * || toState = "/PersonalInfo/RequestName"
        q: * {$incorrect * *date} * || toState = "/PersonalInfo/RequestDob"
        q: * {$incorrect * email} * || toState = "/PersonalInfo/RequestEmail"
        
        state: Incorrect
            q: ($incorrect/$localDisagree)
            a: That's all right! I will just ask you a few questions to update your info. 😊
            go!: /PersonalInfo/RequestDocument
    
    state: RequestDocument
        script:
            $analytics.setSessionResult("Ask user Document for eligibility");
        random:
            a: Please, enter your document number. 🆔
        
        state: GetDocument
            q: * @duckling.number::number *
            script:
                $session.user.documentNumber = validateDocument($parseTree._number, 11);
            if: $session.user.documentNumber
                script:
                    $analytics.setSessionResult("Get user Document for eligibility");
                go!: /PersonalInfo/PersonalInfoConfirmation
            else:
                a: Sorry, that's not a valid document number. Please, try again.

    state: RequestName
        script:
            $analytics.setSessionResult("Ask user name for eligibility");
        a: Please enter your full name.

        state: GetName
            q: * $Name $Surname *
            script:
                $analytics.setSessionResult("Get user name for eligibility");
                $session.user.name = $parseTree._Name.name + " " + $parseTree._Surname;
            go!: /PersonalInfo/PersonalInfoConfirmation
        
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $analytics.setSessionResult("Ask user name for eligibility: unrecognized response");
            if: countRepeatsInRow() < 2
                a: Unfortunately, that doesn't look like a name to me. Please enter your full name. 😢            else:
                script:
                    $session.user.name = $request.query;
                go!: /PersonalInfo/PersonalInfoConfirmation
    
    state: RequestDob
        script:
            $analytics.setSessionResult("Ask user birthdate for eligibility");
        a: Please enter your date of birth.

        state: GetDob
            q: * @duckling.date::date *
            script:
                $session.user.birthDate = formatDate($parseTree._date.value);
                $temp.timestamp = $parseTree._date.timestamp;
            if: validateDate($temp.timestamp)
                go!: /PersonalInfo/PersonalInfoConfirmation
            else:
                go!: ../CatchAll
        
        state: CatchAll || noContext = true
            event: noMatch
            if: countRepeatsInRow() < 2
                a: Sorry, but I need a valid date in order to renew your policy. Please enter your date of birth. 😢
            else:
                a: I'm sorry, but I still couldn't understand what you meant. 😟
                go!: /ContactAgent
    
    state: RequestEmail
        script:
            $analytics.setSessionResult("Ask user email for eligibility");
        a: Please enter your email address.

        state: GetEmail
            q: * @duckling.email::email *
            script:
                $analytics.setSessionResult("Get user email for eligibility");
                $session.user.email = $parseTree._email;
            go!: /PersonalInfo/PersonalInfoConfirmation
        
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $analytics.setSessionResult("Ask user email for eligibility: unrecognized response");
            if: countRepeatsInRow() < 2
                a: Sorry, but I need a valid email in order to renew your policy. Please enter your email address. 😢
            else:
                a: I'm sorry, but I still couldn't understand what you meant. 😟
                go!: /ContactAgent

    state: UserScreening
        if: paymentApi.checkEligibility($session.user.documentNumber)
            script:
                $analytics.setSessionResult("Insurance approved");
            a: Thank you! We now have almost everything and are ready to move to the last step.
            go!: /Payment/GenerateLink
        else:
            script:
                $analytics.setSessionResult("Insurance disapproved");
            a: I'm afraid I'm encountering some difficulties in renewing the insurance policy. 😣❌
            go!: /ContactAgent
