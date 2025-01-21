require: requirements.sc

theme: /

    state: Introduction
        intent!: /Hi
        q!: $regex</start>
        script:
            $jsapi.startSession();
            $analytics.setSessionResult("Greetings");
            var secret = $secrets.get("TEST", "not so secret");
            var env = $env.get("TEST", "a default value");
        a: Hi! It's Mary, an insurance specialist from SureThing Insurance ✨
                I'm here to help you in renewing your home insurance. 💼💪
        random: 
            a: Should we continue? 💬
            a: Do you want to continue the dialogue with me? 💬
        buttons:
            "Yes" -> /Proposal/CheckProductStatus
            "No" -> /Objection/NotInterested
        q: $localAgree || toState = "/Proposal/CheckProductStatus"
        q: $localDisagree || toState = "/Objection/NotInterested"

    state: Gratitude
        intent!: /Gratitude
        random:
            a: You're welcome! 😊
            a: No problem at all! 👍
            a: Happy to help! 😄
        go!: /Goodbye

    state: Goodbye
        intent!: /Goodbye
        random:
            a: Best wishes! 🌟
            a: Goodbye, and all the best! 👋✨
            a: Take care! 🍀
        script:
            $jsapi.stopSession();
        
    state: Clarity || noContext = true
        intent!: /Clarity
        random:
            a: Okay, sounds like you want to speak to a sales agent. 🤝
            a: Got it, you want to chat with one of our agents. 🤝
        go!: /ContactAgent
        
    state: ContactAgent
        script:
            $analytics.setSessionResult("Ask user to contact a remote sales agent");
            $analytics.setSessionData("Asked the user to contact an agent", "Yes");
        random:
            a: In that case, I suggest you contact your nearest a remote sales agent to continue discussing your insurance policy. You can find one via this [website](https://example.com). 🌐
        go!: /Goodbye
                
    state: GlobalCatchAll || noContext = true
        event!: noMatch
        if: countRepeatsInRow() < CATCH_ALL_LIMIT - 1
            random:
                a: Unfortunately, I didn't get it. Could you please rephrase that?
                a: I'm sorry, but I don't understand. Could you say that in a different way?
                a: I'm not quite sure I understand. Can you reword that?
        else:
            a: I'm sorry, but I still couldn't understand what you meant.
            go!: /ContactAgent
            
    state: FileEvent || noContext = true
        event!: fileEvent
        script:
            $temp.fileType = $request.data.eventData[0].type;
        if: $temp.fileType === "audio"
            a: You sent me an audio file, but I can't listen to it yet. 🎧
        elseif: $temp.fileType === "image"
            a: Image processing isn't something I can do yet, but I'm working on it. 🖼️
        elseif: $temp.fileType === "video"
            a: I've received your video, but I'm not yet familiar with how to work with it. 🎥
        else:
            a: I've got this file, but I don't know how to work with it yet. 📁
        a: Could you just type out your reply, please? 📝

    state: LimitHandler || noContext = true
        event!: lengthLimit
        event!: timeLimit
        event!: nluSystemLimit
        if: countRepeatsInRow() < CATCH_ALL_LIMIT - 1
            random:
                a: Sorry, I'm a bit lost. Please text that again, but keep it short. 💬
                a: I don't get what you're saying. Can you say it more briefly? 💬
        else:
            a: I'm sorry, but I still couldn't understand what you meant. 😟
            go!: /ContactAgent