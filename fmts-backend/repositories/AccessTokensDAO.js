const fs = require('fs');

//5 minutes in milliseconds
const delay = 30000000; //Changed to 500 minutes
const tokenFile = './data/token.json';

let read_json_file = () => {
    return fs.readFileSync(tokenFile);
}

let writeToken = (tokenList) => {
    fs.writeFileSync(tokenFile, JSON.stringify(tokenList));
}

let loadToken = () => {
    let tokenList;
    try {
        tokenList = read_json_file();
        return tokenList;
    } catch (error) {
        console.log(error);
        return null;
    }
}

let createCurrentTokenObject = (tokenString) => {

    return {
        token: tokenString.toString(),
        timestamp: (new Date()).getTime().toString()
    }
}

exports.updateTokenValidity = (token) => {
    console.log('Update token', token)
    let tokenList = [];
    tokenList = JSON.parse(loadToken());
    console.log('Old TokenList=', tokenList);
    if (tokenList !== null) {
        for (let i = 0; i < tokenList.length; ++i) {
            let tokenObj = tokenList[i];
            if (tokenObj.token == token) {
                console.log('Found it');
                tokenObj.timestamp = (new Date()).getTime().toString();
            }
        }
    }

    writeToken(tokenList);
    console.log('Updated TokenList=', tokenList);
}

// check whether token is still valid for operations
exports.checkToken = (oldtoken) => {
    let tokenList = [];
    tokenList = JSON.parse(loadToken());
    if (tokenList !== null) {
        for (let i = 0; i < tokenList.length; ++i) {
            let token = tokenList[i];
            if (token.token == oldtoken) {
                // if token is used within 5 minutes it is valid, 
                // or new authentication is needed
                let t = parseInt(token.timestamp) + delay;
                if ((parseInt(token.timestamp) + delay) > (new Date().getTime())) {
                    return true;
                }
            }
        }
    }

    return false;
}

// add new token to list and remove previous token if exists 
exports.addNewToken = (oldToken, currToken) => {
    let tokenList = [];
    let deleteToken;
    tokenList = JSON.parse(loadToken());
    if (tokenList !== null) {
        if (oldToken != undefined || oldToken != null) {
            tokenList = removeToken(oldToken, tokenList);
        } else {
            tokenList = [];
        }
        let tokenObj = createCurrentTokenObject(currToken);
        console.log('Adding new token: ' + tokenObj);
        tokenList.push(tokenObj);

        writeToken(tokenList);
    }
}

function removeToken(token, tokenList) {
    tokenList = tokenList.filter(curr => curr.token != token);

    return tokenList;
}
