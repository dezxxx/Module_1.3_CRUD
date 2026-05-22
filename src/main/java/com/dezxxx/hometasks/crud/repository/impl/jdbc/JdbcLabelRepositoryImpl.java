package com.dezxxx.hometasks.crud.repository.impl.jdbc;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.repository.LabelRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLabelRepositoryImpl implements LabelRepository {

    private final Connection connection;

    public JdbcLabelRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Label save(Label label) {

        String sql = """
                INSERT INTO label (name)
                VALUES (?)
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {

            ps.setString(1, label.getName());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    label.setId(rs.getLong(1));
                }
            }

            return label;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error saving label",
                    e
            );
        }
    }

    @Override
    public List<Label> findAll() {

        String sql = """
                SELECT id,
                       name
                FROM label
                ORDER BY id
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql);

                ResultSet rs = ps.executeQuery()) {

            List<Label> labels = new ArrayList<>();

            while (rs.next()) {

                Label label = new Label();
                label.setId(rs.getLong("id"));
                label.setName(rs.getString("name"));

                labels.add(label);
            }

            return labels;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error loading labels",
                    e
            );
        }
    }

    @Override
    public Optional<Label> findById(Long id) {

        String sql = """
                SELECT id,
                       name
                FROM label
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Label label = new Label();
                    label.setId(rs.getLong("id"));
                    label.setName(rs.getString("name"));

                    return Optional.of(label);
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding label",
                    e
            );
        }
    }

    @Override
    public Label update(Label label) {

        String sql = """
                UPDATE label
                SET name = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setString(1, label.getName());
            ps.setLong(2, label.getId());

            ps.executeUpdate();

            return label;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error updating label",
                    e
            );
        }
    }

    @Override
    public void deleteById(Long id) {

        String sql = """
                DELETE FROM label
                WHERE id = ?
                """;

        try (PreparedStatement ps =
                connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error deleting label",
                    e
            );
        }
    }
}
