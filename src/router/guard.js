import { useAuthStore } from "@/stores/useAuthStore";

export function setupRouterGuard(router) {
  router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    const requiresAuth = to.meta.requiresAuth;
    const allowedRoles = to.meta.roles;

    if (requiresAuth && !authStore.isLoggedIn) {
      next({ name: "login" });
      return;
    }

    if (allowedRoles && authStore.user && !allowedRoles.includes(authStore.user.role)) {
      next({ name: "home" });
      return;
    }

    if ((to.name === "login" || to.name === "register") && authStore.isLoggedIn) {
      next({ name: "home" });
      return;
    }

    next();
  });
}
