import {GET, POST, PUT, DELETE} from "../auth/fetch-action";

const BaseURL = '/v1/api/categories'

const createTokenHeader = (token) => {
    return {
        headers: {
        'Authorization': 'Bearer ' + token
        }
    }
}

export const getCategoryHandler = (token, categoryId) => {
    const URL = '/' + categoryId;
    const response = GET(BaseURL + URL, createTokenHeader(token));
    return response;
}

export const getAllCategoriesHandler = (token) => {
    const response = GET(BaseURL, createTokenHeader(token));
    return response;
}

export const createCategoryHandler = (token, categoryName, hexColor) => {

    const object = {categoryName, hexColor};
    const response = POST(BaseURL, object, createTokenHeader(token));
    return response;
}

export const deleteCategoryHandler = (token, categoryId) => {
    const URL = '/' + categoryId;
    const response = DELETE(BaseURL + URL, createTokenHeader(token));
    return response;
}