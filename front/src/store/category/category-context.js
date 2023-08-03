import React, { createContext, useState } from 'react';
import * as authAction from '../auth/auth-action'; 
import * as categoryAction from './category-action';

export const CategoryContext = createContext({
    currentCategory: null,
    categories: [],
    setCategory: (categoryId) => {},
    getCategories: () => {},
    createCategory: (categoryName, hexColor) => {},
    deleteCategory: (categoryId) => {},
});

export const CategoryContextProvider = ({children}) => {

    const [currentCategory, setCurrentCategory] = useState(null);
    const [categories, setCategories] = useState([]);

    const setCategory = (categoryId) => {
        categories.forEach((category) => {
            if (category.id === categoryId) {
                setCurrentCategory(category);
            }
        });
    }

    const getCategories = () => {
        const token = authAction.retrieveStoredToken();
        const data = categoryAction.getAllCategoriesHandler(token);
        data.then((result) => {
            if (result !== null && result.data.code === 200) {
                setCategories(result.data.data);
            }else{
                window.alert(result.data.message);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }

    const createCategory = (categoryName, hexColor) => {
        const token = authAction.retrieveStoredToken();
        const data = categoryAction.createCategoryHandler(token, categoryName, hexColor);
        data.then((result) => {
            if (result !== null && result.status === 201) {
                window.alert("카테고리가 생성되었습니다.");
                let location = result.headers['location'];
                let splitLocation = location.split("/");  // ["", "v1", "api", "categories", "5"]
                let id = splitLocation[splitLocation.length - 1];
                
                setCategories([...categories, {id: id, categoryName: categoryName, color: hexColor}])
            }else{
                window.alert(result.data.message);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }

    const deleteCategory = (categoryId) => {
        const token = authAction.retrieveStoredToken();
        const data = categoryAction.deleteCategoryHandler(token, categoryId);
        data.then((result) => {
            if (result !== null && result.status === 204) {
                window.alert("카테고리가 삭제되었습니다.");
                setCategories(categories.filter((category) => category.id !== categoryId));
            }else{
                window.alert(result.data.message);
            }
        }).catch((err) => {
            window.alert('에러가 발생했습니다. 다시 시도해주세요.');
        });
    }


    const contextValue = {
        currentCategory,
        categories,
        setCategory,
        getCategories,
        createCategory,
        deleteCategory,
    }

    return (
        <CategoryContext.Provider value={contextValue}>
            {children}
        </CategoryContext.Provider>
    );
}

export default CategoryContext;