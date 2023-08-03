import MainNavigation from './navigation/MainNavigation';
import '../public/css/layout.css';

const Layout = (props) => {

    return (
        <div>
        <MainNavigation/>
        <div className='main'>{props.children}</div>
        </div>
    );
}

export default Layout;