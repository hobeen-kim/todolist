import {GET, POST, DELETE, PATCH} from "../auth/fetch-action";

const BaseURL = '/v1/api/todos'

const createTokenHeader = (token) => {
    return {
        headers: {
        'Authorization': 'Bearer ' + token
        }
    }
}

export const getTodosHandler = (token, categoryId, from, to, searchType) => {
    let URL = '?categoryId=' + categoryId;
    if(from !== null){
        URL += '&from=' + from;
    }
    if(to !== null){
        URL += '&to=' + to;
    }
    if(searchType !== null){
        URL += '&searchType=' + searchType;
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