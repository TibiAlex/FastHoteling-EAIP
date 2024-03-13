package com.example.back.service;

import com.example.back.handlers.NotAdminException;
import com.example.back.handlers.NotLoggedInException;
import com.example.back.handlers.RoomAlreadyExists;
import com.example.back.handlers.UserNotFoundException;
import com.example.back.model.Role;
import com.example.back.model.entities.AppUser;
import com.example.back.model.entities.Room;
import com.example.back.model.requestsAndResponses.AddRoomRequest;
import com.example.back.repository.AppUserRepo;
import com.example.back.repository.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    private final AppUserRepo appUserRepo;
    private final RoomRepo roomRepo;

    /**
     * fucntie ce cauta un utilizator dupa username
     * @param username un string
     * @return un user
     * @throws UsernameNotFoundException exceptie
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        String.format("Utilizatorul cu numele %s nu a fost gasit", username)
                ));
    }

    /**
     * functie de adaugat o camera in baza de date
     * @param req  private Integer number;
     *              private Integer occupancy;
     * @return mesaj de success sau de eroare
     */
    public ResponseEntity<?> addRoom(AddRoomRequest req) {
        if (req.getNumber() == null || req.getOccupancy() == null) {
            System.out.println("AddRoom error: Nu pot fi campuri goale");
            return ResponseEntity.ok("Nu pot fi campuri goale");
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            Long me_id = ((AppUser)principal).getId();
            Optional<AppUser> me_user = appUserRepo.findById(me_id);
            if (me_user.isPresent()) {
                if (me_user.get().getRole() == Role.ADMIN) {
                    Optional<Room> room =  roomRepo.findByNumber(req.getNumber());
                    if(room.isEmpty()) {
                        roomRepo.save(new Room(req.getNumber(), req.getOccupancy()));
                        System.out.println("Add room success: Camera " + req.getNumber() + "a fost adaugata!");
                        return ResponseEntity.ok("Camera a fost adaugata!");
                    } else {
                        throw new RoomAlreadyExists();
                    }
                } else {
                    throw new NotAdminException();
                }
            } else {
                throw new UserNotFoundException();
            }
        } else {
            throw new NotLoggedInException();
        }
    }

    /**
     * metoda ce verifica daca un user este admin
     * @return true daca este sau fals daca nu
     */
    public ResponseEntity<?> isAdmin() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            Long me_id = ((AppUser) principal).getId();
            Optional<AppUser> me_user = appUserRepo.findById(me_id);
            if (me_user.isPresent()) {
                if (me_user.get().getRole() == Role.ADMIN) {
                    return ResponseEntity.ok(true);
                } else {
                    return ResponseEntity.ok(false);
                }
            } else {
                throw new UserNotFoundException();
            }
        } else {
            throw new NotLoggedInException();
        }
    }
}
