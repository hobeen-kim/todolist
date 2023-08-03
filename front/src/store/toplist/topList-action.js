import {GET, POST, PUT, DELETE} from "../auth/fetch-action";

const BaseURL = '/v1/api/toplist'

const createTokenHeader = (token) => {
    return {
        headers: {
        'Authorization': 'Bearer ' + token
        }
    }
}

export const getTopListHandler = (token, categoryId, from, to, isDone) => {
    let URL = '?categoryId=' + categoryId;
    if(from !== null){
        URL += '&from=' + from;
    }
    if(to !== null){
        URL += '&to=' + to;
    }
    if(isDone !== null){
        URL += '&isDone=' + isDone;
    }
    const response = GET(BaseURL + URL, createTokenHeader(token));
    return response;
}