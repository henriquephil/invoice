import { Link } from "@tanstack/react-router";
import styled from "@emotion/styled";
import { useAccountStore } from "#/store/accountStore";
import { logout } from "#/api/authClient";

const StyledSidebar = styled.aside`
    flex-basis: 18rem;
    flex-shrink: 0;
    background-color: var(--surface-dark);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
`;

const UserProfile = styled.div`
    display: flex;
    align-items: center;
    gap: 1rem;
    padding-left: 0.5rem;
    padding-right: 0.5rem;
    padding: 2rem;
    height: 5rem;
`;

const Avatar = styled.div`
    width: 2.5rem;
    height: 2.5rem;
    border-radius: 9999px;
    background-image: linear-gradient(to top right, #27272a, #52525b);
    border: 1px solid var(--border-glass);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.75rem;
    font-weight: 700;
    color: white;
`;

const UserInfo = styled.div`
    overflow: hidden;
`;

const UserName = styled.p`
    font-size: 0.875rem;
    font-weight: 600;
    color: white;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
`;

const UserEmail = styled.p`
    font-size: 0.75rem;
    opacity: 0.4;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
`;

const AccountInfo = styled(Link)`
    padding: 1rem;
    border-top: 1px solid var(--border-glass);
    border-bottom: 1px solid var(--border-glass);

    display: block;
    text-decoration: none;
    transition: background-color 0.2s, border-color 0.2s;
    padding: 2rem;
    background-color: rgba(255, 255, 255, 0.02);

    &:hover {
        cursor: pointer;
        background-color: rgba(255, 255, 255, 0.05);
    }
`;

const AccountLabel = styled.span`
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    opacity: 0.3;
    margin-bottom: 0.5rem;
    color: #d4d4d8;
`;

const AccountNameContainer = styled.div`
    display: flex;
    align-items: center;
    justify-content: space-between;
`;

const AccountName = styled.span`
    font-size: 0.875rem;
    font-weight: 500;
    color: #d4d4d8;
    font-style: italic;
`;

const Nav = styled.nav`
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    gap: 1.5rem;
    padding: 2rem;
`;

const NavLink = styled(Link)`
  text-decoration-color: rgba(50, 143, 151, 0.4);
  text-decoration-thickness: 1px;
  text-underline-offset: 2px;
  position: relative;
  display: inline-flex;
  align-items: center;
  text-decoration: none;
  color: var(--text-primary);
  opacity: 0.5;
  font-size: 0.9rem;

  &::after {
    content: "";
    position: absolute;
    left: 0;
    bottom: -6px;
    width: 100%;
    height: 1px;
    transform: scaleX(0);
    transform-origin: left;
    background: linear-gradient(90deg, var(--accent-primary), #7ed3bf);
    transition: transform 170ms ease;
    box-shadow: 0 0 4px var(--accent-primary);
  }

  &:hover,
  &.active {
    text-shadow: 0 0 4px var(--accent-primary);
    opacity: 1;
  }

  &:hover::after,
  &.active::after {
    transform: scaleX(1);
  }
`;

const BottomLinks = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: space-between;
`;

const StyledLink = styled.a`
    font-size: 0.75rem;
    opacity: 0.4;
    text-transform: uppercase;
    letter-spacing: 0.1em;
    font-weight: 600;
    &:hover {
        opacity: 1;
    }
`;

export default function Menu() {
    const { activeAccount } = useAccountStore();

    return (
        <StyledSidebar>
            <UserProfile onClick={() => logout()}>
                <Avatar>
                    HP
                </Avatar>
                <UserInfo>
                    <UserName>Henrique Phil</UserName>
                    <UserEmail>admin@hphil.dev</UserEmail>
                </UserInfo>
            </UserProfile>

            <AccountInfo to="/account">
                <AccountLabel>Active Account</AccountLabel>
                <AccountNameContainer>
                    <AccountName>{activeAccount ? activeAccount.name : 'No Active Account'}</AccountName>
                </AccountNameContainer>
            </AccountInfo>

            <Nav>
                {activeAccount && (
                    <>
                    <NavLink to="/invoices">Invoices</NavLink>
                    <NavLink to="/customers">Customers</NavLink>
                    <NavLink to="/items">Products & Services</NavLink>
                    </>
                )}
            </Nav>
        
            <BottomLinks>
                <StyledLink href="https://github.com/henriquephil/invoice" target="_blank">About</StyledLink>
                <StyledLink href="https://github.com/henriquephil/invoice" target="_blank">Github</StyledLink>
            </BottomLinks>
        </StyledSidebar>
    )
}
