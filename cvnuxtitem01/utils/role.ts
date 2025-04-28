export const currentRole = ():Role | null => {
    return takeAccessToken()?.role as Role || null
}

export const hasRole = (...roles:Role[]):boolean =>{
    const current:Role | null = currentRole()
    return !!current && roles.includes(current)
}

export const isRole = (role:Role):boolean => currentRole() == role

export const useRole = () => {
    const role = currentRole()
    return {
        role,
        isAdmin: role === Role.Admin,
        isUser: role === Role.User,
        hasRole: (...roles: Role[]) => hasRole(...roles)
    }
}
