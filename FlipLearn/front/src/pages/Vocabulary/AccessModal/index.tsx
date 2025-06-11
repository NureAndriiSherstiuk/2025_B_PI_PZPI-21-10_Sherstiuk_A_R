import axios from "axios";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next"; // Added import for translations

interface User {
  id: string;
  username: string;
  email: string;
}

interface SelectedUser extends User {
  access: string;
}

export const AccessModal = () => {
  const { t } = useTranslation(); // Initialize translation hook
  const [searchQuery, setSearchQuery] = useState("");
  const [users, setUsers] = useState<User[]>([]);
  const [isSearched, setIsSearched] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [offset, setOffset] = useState(0);
  const [selectedUsers, setSelectedUsers] = useState<SelectedUser[]>([]);
  const [userWithAccess, setUserWithAccess] = useState<SelectedUser[]>([]);
  const [originalUserAccess, setOriginalUserAccess] = useState<SelectedUser[]>([]);
  const [usersToDelete, setUsersToDelete] = useState<SelectedUser[]>([]);
  const { id } = useParams();
  const token = localStorage.getItem("token");

  const toggleUserSelection = (user: User) => {
    setSelectedUsers((prevSelected) => {
      const isAlreadySelected = prevSelected.some((u) => u.id === user.id);
      if (isAlreadySelected) {
        return prevSelected.filter((u) => u.id !== user.id);
      } else {
        return [...prevSelected, { ...user, access: "Reader" }];
      }
    });
  };

  const updateUserAccess = (userId: string, newAccess: string) => {
    // First check if this is a user who already has access
    const existingUserIndex = userWithAccess.findIndex((user) => user.id === userId);

    if (existingUserIndex !== -1) {
      setUserWithAccess((prevUsers) =>
        prevUsers.map((user) => (user.id === userId ? { ...user, access: newAccess } : user))
      );
    } else {
      // Otherwise it's a newly selected user
      setSelectedUsers((prevSelected) =>
        prevSelected.map((user) => (user.id === userId ? { ...user, access: newAccess } : user))
      );
    }
  };

  const removeUserAccess = (userId: string) => {
    const userToRemove = userWithAccess.find((user) => user.id === userId);

    if (userToRemove) {
      // Add to users to delete list
      setUsersToDelete((prev) => [...prev, userToRemove]);
      // Remove from displayed users with access
      setUserWithAccess((prev) => prev.filter((user) => user.id !== userId));
    } else {
      // Otherwise just remove from selected users
      setSelectedUsers((prev) => prev.filter((user) => user.id !== userId));
    }
  };

  const fetchAccesses = () => {
    axios
      .get(`https://localhost:7288/dictionary-access?dictionaryId=${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {
        setUserWithAccess(res.data);
        // Store original state for comparison when updating
        setOriginalUserAccess(res.data);
      });
  };

  const fetchUsers = async (query: string, currentOffset: number, append = false) => {
    if (!query.trim()) return;

    setIsLoading(true);
    try {
      const response = await fetch(
        `https://localhost:7288/User/byUsernameEmail?query=${encodeURIComponent(
          query
        )}&offset=${currentOffset}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch users");
      }

      const data = await response.json();

      // Filter found users to exclude those who already have access AND selected users
      const filteredData = data.filter(
        (user: User) =>
          !userWithAccess.some((accessUser) => accessUser.id === user.id) &&
          !selectedUsers.some((selectedUser) => selectedUser.id === user.id)
      );

      if (append) {
        setUsers((prevUsers) => [...prevUsers, ...filteredData]);
      } else {
        setUsers(filteredData);
        setIsSearched(true);
      }

      setOffset(currentOffset + 1);
    } catch (error) {
      console.error("Error fetching users:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = () => {
    setOffset(0);
    fetchUsers(searchQuery, 0);
  };

  const updateAccess = () => {
    setIsSubmitting(true);

    // Prepare the request body according to the specified format
    const requestBody = {
      dictionaryId: Number(id),
      // New users to add access
      accessToInsert: selectedUsers.map((user) => ({
        userId: Number(user.id),
        access: user.access,
      })),
      // Existing users whose access level has changed
      accessToUpdate: userWithAccess
        .filter((user) => {
          const original = originalUserAccess.find((u) => u.id === user.id);
          return original && original.access !== user.access;
        })
        .map((user) => ({
          userId: Number(user.id),
          access: user.access,
        })),
      // Users to delete (stored in usersToDelete state)
      accessToDelete: usersToDelete.map((user) => ({
        userId: Number(user.id),
        access: user.access,
      })),
    };

    console.log("Request body:", requestBody); // Debug log

    axios
      .put(`https://localhost:7288/dictionary-access`, requestBody, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      })
      .then(() => {
        // Reset states after successful update
        setSelectedUsers([]);
        setUsersToDelete([]);
        fetchAccesses(); // Refresh the list of users with access
        // Re-search to update the found users list
        if (searchQuery.trim()) {
          handleSearch();
        }
      })
      .catch((error) => {
        console.error("Error updating access:", error);
      })
      .finally(() => {
        setIsSubmitting(false);
      });
  };

  // Re-filter users when selectedUsers changes
  useEffect(() => {
    if (isSearched && searchQuery.trim()) {
      fetchUsers(searchQuery, 0);
    }
  }, [selectedUsers]);

  useEffect(() => {
    fetchAccesses();
  }, []);

  return (
    <div className="flex flex-col gap-5 fixed bg-white z-5 w-[70%] rounded-2xl p-4 top-1/2 right-1/2 translate-x-1/2 -translate-y-1/2 overflow-y-scroll max-h-[500px]">
      <h2 className="text-xl font-bold">{t("access.title")}</h2>
      <div className="flex gap-2 flex-col items-end">
        <input
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder={t("access.searchPlaceholder")}
          className="w-full p-4 rounded-md border-0 bg-[#94a0fe70] outline-none"
          type="text"
          autoComplete="off"
        />
        <button
          onClick={handleSearch}
          className="bg-[#D9D9D9] px-4 py-2 rounded-md cursor-pointer hover:opacity-75"
          disabled={isLoading || isSubmitting || !searchQuery.trim()}
        >
          {isLoading ? t("access.loading") : t("access.searchButton")}
        </button>
      </div>
      {isSearched && (
        <div className="mt-2">
          {users.length !== 0 ? (
            <>
              <h3 className="font-medium mb-2">{t("access.foundUsers")}</h3>
              <div className="max-h-64 overflow-y-auto border rounded-md">
                {users.map((user) => (
                  <div
                    key={user.id}
                    className="flex items-center p-3 border-b hover:bg-gray-100 cursor-pointer"
                    onClick={() => toggleUserSelection(user)}
                  >
                    <div>
                      <p className="font-medium">{user.username || "Користувач"}</p>
                      <p className="text-sm text-gray-600">{user.email}</p>
                    </div>
                  </div>
                ))}
              </div>
            </>
          ) : (
            t("access.noUsersFound")
          )}
        </div>
      )}
      {selectedUsers.length > 0 && (
        <div className="mt-4">
          <h3 className="font-medium mb-2">{t("access.selectedUsers")}</h3>
          <div className="border rounded-md p-3">
            {selectedUsers.map((user) => (
              <div key={user.id} className="flex items-center justify-between p-2 border-b">
                <div>
                  <p className="font-medium">{user.username || "Користувач"}</p>
                  <p className="text-sm text-gray-600">{user.email}</p>
                </div>
                <select
                  value={user.access}
                  onChange={(e) => updateUserAccess(user.id, e.target.value)}
                  className="border p-1 rounded-md mx-3"
                >
                  <option value="Reader">{t("access.reader")}</option>
                  <option value="CoAuthor">{t("access.coAuthor")}</option>
                </select>
                <button onClick={() => removeUserAccess(user.id)} className="text-red-500 hover:text-red-700">
                  {t("access.remove")}
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
      {userWithAccess.length > 0 && (
        <div className="mt-4">
          <h3 className="font-medium mb-2">{t("access.usersWithAccess")}</h3>
          <div className="border rounded-md p-3">
            {userWithAccess.map((user) => (
              <div key={user.id} className="flex items-center justify-between p-2 border-b">
                <div>
                  <p className="font-medium">{user.username || "Користувач"}</p>
                  <p className="text-sm text-gray-600">{user.email}</p>
                </div>
                <select
                  value={user.access}
                  onChange={(e) => updateUserAccess(user.id, e.target.value)}
                  className="border p-1 rounded-md mx-3"
                >
                  <option value="Reader">{t("access.reader")}</option>
                  <option value="CoAuthor">{t("access.coAuthor")}</option>
                </select>
                <button onClick={() => removeUserAccess(user.id)} className="text-red-500 hover:text-red-700">
                  {t("access.remove")}
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
      <button
        className="bg-[#D9D9D9] hover:opacity-75 rounded-[10px] p-3 disabled:opacity-50 disabled:cursor-not-allowed"
        onClick={updateAccess}
        disabled={
          isSubmitting ||
          (selectedUsers.length === 0 &&
            usersToDelete.length === 0 &&
            !userWithAccess.some((user) => {
              const original = originalUserAccess.find((u) => u.id === user.id);
              return original && original.access !== user.access;
            }))
        }
      >
        {isSubmitting
          ? t("access.process")
          : selectedUsers.length > 0 ||
            usersToDelete.length > 0 ||
            userWithAccess.some((user) => {
              const original = originalUserAccess.find((u) => u.id === user.id);
              return original && original.access !== user.access;
            })
          ? t("access.grantUpdate")
          : t("access.updateOnly")}
      </button>
    </div>
  );
};
