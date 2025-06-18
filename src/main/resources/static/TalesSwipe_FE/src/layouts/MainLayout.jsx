import Navbar from "../components/Navbar";
import { Outlet } from "react-router-dom";

export default function MainLayout({ children }) {
    return (
        <>
            <Navbar />
            {children}
            <Outlet />
        </>
    )
}