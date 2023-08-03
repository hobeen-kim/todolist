import { useEffect, useState } from "react";
import { Routes, Route } from 'react-router-dom';
import Category from "../component/category/Category";
import { useContext } from "react";
import CategoryContext from "../store/category/category-context";
import Layout from "../layout/Layout";
import '../public/css/main.css'
import { TopListContextProvider } from '../store/toplist/topList-context';


const Main = () => {

    const categoryCtx = useContext(CategoryContext);

    const deleteCategory = () => {
        const confirm = window.confirm('Are you sure you want to delete this category?');

        if(confirm){

            const deleteId = categoryCtx.currentCategory.id;
            
            categoryCtx.getCategories()?.map((category, index) => {
                if(category.id !== deleteId){
                    categoryCtx.setCategory(category.id);
                }
                }
            );

            categoryCtx.deleteCategory(deleteId);
        }
    }


    return (
        <TopListContextProvider>
            <Layout>
                <div><button className="delete-category" onClick={deleteCategory}>x</button></div>
                <Routes>
                    <Route path="/categories/*" element={<Category/>} />
                </Routes>
            </Layout>
        </TopListContextProvider>
    )
}

export default Main;