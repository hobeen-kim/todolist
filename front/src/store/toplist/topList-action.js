import {GET, POST, DELETE, PATCH} from "../auth/fetch-action";

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

export const updateTopListHandler = (token, topListId, title, content, categoryId, status) => {
    const object = {title, content, categoryId, status};
    let URL = '/' + topListId;
    const response = PATCH(BaseURL + URL, object, createTokenHeader(token));
    return response;
}

export const createTopListHandler = (token, categoryId, title, content) => {
    const object = {categoryId, title, content};
    const response = POST(BaseURL, object, createTokenHeader(token));
    return response;
}